package com.example.data.repository

import com.example.data.local.SalonDao
import com.example.data.model.Appointment
import com.example.data.model.ClientLoyalty
import com.example.data.model.CustomerFeedback
import com.example.data.model.FinancialTransaction
import com.example.data.model.LoyaltyReward
import com.example.data.model.Product
import com.example.data.model.Professional
import com.example.data.model.SalonService
import com.example.data.model.StockMovement
import com.example.util.CloudBackupBundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalonRepository(private val dao: SalonDao) {

    val appointments: Flow<List<Appointment>> = dao.getAllAppointments()
    val transactions: Flow<List<FinancialTransaction>> = dao.getAllTransactions()
    val professionals: Flow<List<Professional>> = dao.getAllProfessionals()
    val activeProfessionals: Flow<List<Professional>> = dao.getActiveProfessionals()
    val services: Flow<List<SalonService>> = dao.getAllServices()
    val loyaltyClients: Flow<List<ClientLoyalty>> = dao.getAllLoyaltyClients()
    val rewards: Flow<List<LoyaltyReward>> = dao.getAllRewards()
    val products: Flow<List<Product>> = dao.getAllProducts()
    val stockMovements: Flow<List<StockMovement>> = dao.getAllStockMovements()
    val feedbacks: Flow<List<CustomerFeedback>> = dao.getAllFeedbacks()

    fun getAppointmentsForDate(date: String): Flow<List<Appointment>> {
        return dao.getAppointmentsForDate(date)
    }

    suspend fun insertAppointment(appointment: Appointment): Long {
        return dao.insertAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: Appointment) {
        dao.updateAppointment(appointment)
    }

    suspend fun deleteAppointment(appointment: Appointment) {
        dao.deleteAppointment(appointment)
    }

    suspend fun confirmAppointment(id: Long) {
        val current = dao.getAppointmentById(id) ?: return
        dao.updateAppointment(current.copy(status = "CONFIRMADO"))
    }

    suspend fun completeAppointment(appointment: Appointment, paymentMethod: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 1. Update appointment status
        val updatedAppointment = appointment.copy(
            status = "CONCLUIDO",
            paymentStatus = if (appointment.paymentStatus.startsWith("PAGO")) appointment.paymentStatus else "PAGO_$paymentMethod"
        )
        dao.updateAppointment(updatedAppointment)

        // 2. Register financial transaction
        val tx = FinancialTransaction(
            description = "${appointment.serviceName} - ${appointment.clientName}",
            amount = appointment.servicePrice,
            type = "RECEITA",
            category = "SERVICO",
            date = today,
            paymentMethod = paymentMethod,
            appointmentId = appointment.id
        )
        dao.insertTransaction(tx)

        // 3. Update client loyalty
        val pointsToEarn = (appointment.servicePrice / 10).toInt().coerceAtLeast(5)
        val existingClient = dao.getClientLoyaltyByPhone(appointment.clientPhone)

        val updatedClient = if (existingClient != null) {
            val newTotalPoints = existingClient.totalPoints + pointsToEarn
            val newVisits = existingClient.totalVisits + 1
            val newSpent = existingClient.totalSpent + appointment.servicePrice
            existingClient.copy(
                clientName = appointment.clientName,
                totalPoints = newTotalPoints,
                totalVisits = newVisits,
                lastVisitDate = today,
                totalSpent = newSpent,
                tier = calculateTier(newTotalPoints)
            )
        } else {
            ClientLoyalty(
                clientName = appointment.clientName,
                clientPhone = appointment.clientPhone,
                totalPoints = pointsToEarn,
                tier = calculateTier(pointsToEarn),
                totalVisits = 1,
                lastVisitDate = today,
                totalSpent = appointment.servicePrice
            )
        }
        dao.insertOrUpdateLoyalty(updatedClient)
    }

    suspend fun processOnlinePayment(appointmentId: Long, method: String) {
        val apt = dao.getAppointmentById(appointmentId) ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val updated = apt.copy(
            paymentStatus = "PAGO_$method",
            status = if (apt.status == "AGENDADO") "CONFIRMADO" else apt.status
        )
        dao.updateAppointment(updated)

        // Insert payment transaction
        val tx = FinancialTransaction(
            description = "Pagamento Online (${method}) - ${apt.serviceName} (${apt.clientName})",
            amount = apt.servicePrice,
            type = "RECEITA",
            category = "SERVICO",
            date = today,
            paymentMethod = method,
            appointmentId = apt.id
        )
        dao.insertTransaction(tx)
    }

    suspend fun insertTransaction(transaction: FinancialTransaction): Long {
        return dao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: FinancialTransaction) {
        dao.deleteTransaction(transaction)
    }

    suspend fun insertProfessional(professional: Professional): Long {
        return dao.insertProfessional(professional)
    }

    suspend fun updateProfessional(professional: Professional) {
        dao.updateProfessional(professional)
    }

    suspend fun deleteProfessional(professional: Professional) {
        dao.deleteProfessional(professional)
    }

    suspend fun insertService(service: SalonService): Long {
        return dao.insertService(service)
    }

    suspend fun updateService(service: SalonService) {
        dao.updateService(service)
    }

    suspend fun deleteService(service: SalonService) {
        dao.deleteService(service)
    }

    // --- Inventory Products ---
    suspend fun insertProduct(product: Product): Long {
        return dao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        dao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        dao.deleteProduct(product)
    }

    // --- Stock Movements ---
    suspend fun recordStockMovement(
        productId: Long,
        type: String, // "ENTRADA" or "SAIDA"
        quantity: Int,
        reason: String,
        notes: String = ""
    ): Boolean {
        val product = dao.getProductById(productId) ?: return false
        val newStock = if (type == "ENTRADA") {
            product.currentStock + quantity
        } else {
            (product.currentStock - quantity).coerceAtLeast(0)
        }

        dao.updateProduct(product.copy(currentStock = newStock, lastUpdated = System.currentTimeMillis()))

        val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val movement = StockMovement(
            productId = product.id,
            productName = product.name,
            type = type,
            quantity = quantity,
            reason = reason,
            notes = notes,
            date = nowStr
        )
        dao.insertStockMovement(movement)
        return true
    }

    // --- Customer Feedback ---
    suspend fun insertFeedback(feedback: CustomerFeedback): Long {
        return dao.insertFeedback(feedback)
    }

    suspend fun deleteFeedback(feedback: CustomerFeedback) {
        dao.deleteFeedback(feedback)
    }

    suspend fun redeemReward(clientPhone: String, reward: LoyaltyReward): Boolean {
        val client = dao.getClientLoyaltyByPhone(clientPhone) ?: return false
        if (client.totalPoints < reward.pointsRequired) return false

        val updated = client.copy(
            totalPoints = client.totalPoints - reward.pointsRequired,
            tier = calculateTier(client.totalPoints - reward.pointsRequired)
        )
        dao.insertOrUpdateLoyalty(updated)
        return true
    }

    suspend fun insertLoyaltyClient(client: ClientLoyalty) {
        dao.insertOrUpdateLoyalty(client)
    }

    private fun calculateTier(points: Int): String {
        return when {
            points >= 300 -> "VIP_DIAMANTE"
            points >= 150 -> "OURO"
            points >= 60 -> "PRATA"
            else -> "BRONZE"
        }
    }

    suspend fun createCloudBackupBundle(): CloudBackupBundle {
        val apts = appointments.first()
        val txs = transactions.first()
        val profs = professionals.first()
        val servs = services.first()
        val loys = loyaltyClients.first()
        val rews = rewards.first()

        return CloudBackupBundle(
            appointments = apts,
            transactions = txs,
            professionals = profs,
            services = servs,
            loyaltyClients = loys,
            rewards = rews
        )
    }

    suspend fun restoreFromBundle(bundle: CloudBackupBundle) {
        dao.clearAllAppointments()
        dao.clearAllTransactions()
        dao.clearAllProfessionals()
        dao.clearAllServices()
        dao.clearAllLoyaltyClients()
        dao.clearAllRewards()

        dao.insertAllAppointments(bundle.appointments)
        dao.insertAllTransactions(bundle.transactions)
        dao.insertAllProfessionals(bundle.professionals)
        dao.insertAllServices(bundle.services)
        dao.insertAllLoyaltyClients(bundle.loyaltyClients)
        dao.insertAllRewards(bundle.rewards)
    }

    suspend fun processProductPurchase(
        clientName: String,
        clientPhone: String,
        items: List<Pair<Product, Int>>,
        paymentMethod: String,
        isPickup: Boolean,
        notes: String
    ): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var totalAmount = 0.0

        for ((product, qty) in items) {
            val itemTotal = product.salePrice * qty
            totalAmount += itemTotal

            // Record stock movement (SAIDA)
            recordStockMovement(
                productId = product.id,
                type = "SAIDA",
                quantity = qty,
                reason = "Venda Loja Online",
                notes = "Pedido de $clientName ($paymentMethod)"
            )
        }

        // Register Financial Transaction (RECEITA)
        val deliveryType = if (isPickup) "Retirada no Salão" else "Entrega"
        val tx = FinancialTransaction(
            description = "Venda Loja ($deliveryType) - $clientName",
            amount = totalAmount,
            type = "RECEITA",
            category = "VENDA_PRODUTOS",
            date = today,
            paymentMethod = paymentMethod
        )
        dao.insertTransaction(tx)

        // Award points to client (1 point per R$ 10 spent)
        val pointsToEarn = (totalAmount / 10).toInt().coerceAtLeast(1)
        val existingClient = dao.getClientLoyaltyByPhone(clientPhone)
        val updatedClient = if (existingClient != null) {
            val newTotalPoints = existingClient.totalPoints + pointsToEarn
            val newSpent = existingClient.totalSpent + totalAmount
            existingClient.copy(
                clientName = clientName,
                totalPoints = newTotalPoints,
                totalSpent = newSpent,
                tier = calculateTier(newTotalPoints)
            )
        } else {
            ClientLoyalty(
                clientName = clientName,
                clientPhone = clientPhone,
                totalPoints = pointsToEarn,
                tier = calculateTier(pointsToEarn),
                totalVisits = 0,
                lastVisitDate = today,
                totalSpent = totalAmount
            )
        }
        dao.insertOrUpdateLoyalty(updatedClient)
        return true
    }

    suspend fun redeemLoyaltyReward(
        clientPhone: String,
        reward: LoyaltyReward
    ): Boolean {
        val client = dao.getClientLoyaltyByPhone(clientPhone) ?: return false
        if (client.totalPoints < reward.pointsRequired) return false

        val updatedClient = client.copy(
            totalPoints = client.totalPoints - reward.pointsRequired
        )
        dao.insertOrUpdateLoyalty(updatedClient)
        return true
    }
}
