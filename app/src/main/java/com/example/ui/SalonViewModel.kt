package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.ClientAuthUser
import com.example.data.auth.FirebaseAuthService
import com.example.data.local.SalonDatabase
import com.example.data.model.Appointment
import com.example.data.model.ClientLoyalty
import com.example.data.model.CustomerFeedback
import com.example.data.model.FinancialTransaction
import com.example.data.model.LoyaltyReward
import com.example.data.model.Product
import com.example.data.model.Professional
import com.example.data.model.SalonService
import com.example.data.model.StockMovement
import com.example.data.repository.SalonRepository
import com.example.util.CloudBackupBundle
import com.example.util.CloudBackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class SalonTab(val title: String, val iconName: String) {
    SCHEDULE("Agenda", "calendar"),
    SERVICES("Catálogo", "cut"),
    INVENTORY("Estoque", "inventory"),
    TEAM("Equipe & Avaliações", "team"),
    MANAGEMENT("Gestão", "hub"),
    FINANCE("Finanças", "wallet"),
    REPORTS("Relatórios", "chart"),
    LOYALTY_BACKUP("Fidelidade", "sparkle_cloud")
}

enum class AppRole(val label: String) {
    ADMIN("Administrador"),
    CLIENT("Área do Cliente")
}

enum class ClientTab(val title: String) {
    BOOKING("Agendar"),
    SHOP("Loja"),
    MY_APPOINTMENTS("Meus Agendamentos"),
    LOYALTY("Fidelidade")
}

data class CartItem(
    val product: Product,
    val quantity: Int
)

data class MonthlyReportData(
    val monthName: String,
    val totalRevenue: Double,
    val totalExpense: Double,
    val netProfit: Double,
    val profitMarginPercent: Double,
    val completedAppointmentsCount: Int,
    val averageTicket: Double,
    val retentionRatePercent: Double,
    val topServices: List<Pair<String, Double>>,
    val professionalStats: List<ProfessionalPerformance>
)

data class ProfessionalPerformance(
    val professionalName: String,
    val appointmentCount: Int,
    val totalBilled: Double,
    val commissionPercent: Int,
    val commissionAmount: Double
)

class SalonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SalonRepository
    private val firebaseAuthService = FirebaseAuthService(application)

    // ==========================================
    // CLIENT FIREBASE AUTHENTICATION STATE
    // ==========================================
    val clientAuthUser: StateFlow<ClientAuthUser?> = firebaseAuthService.currentUser
    val isClientAuthenticated: StateFlow<Boolean> = clientAuthUser.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isClientAuthLoading = MutableStateFlow(false)
    val isClientAuthLoading: StateFlow<Boolean> = _isClientAuthLoading.asStateFlow()

    private val _clientAuthErrorMessage = MutableStateFlow<String?>(null)
    val clientAuthErrorMessage: StateFlow<String?> = _clientAuthErrorMessage.asStateFlow()

    fun clearClientAuthError() {
        _clientAuthErrorMessage.value = null
    }

    companion object {
        const val ADMIN_EMAIL = "lauraivini13@gmail.com"
        const val ADMIN_NAME = "Laura Ivini"
    }

    // App Role Mode: Admin vs Client
    private val _currentRole = MutableStateFlow(AppRole.CLIENT)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    fun selectRole(role: AppRole) {
        _currentRole.value = role
    }

    // Selected Navigation Tab
    private val _currentTab = MutableStateFlow(SalonTab.SCHEDULE)
    val currentTab: StateFlow<SalonTab> = _currentTab.asStateFlow()

    // Client Navigation Tab
    private val _currentClientTab = MutableStateFlow(ClientTab.BOOKING)
    val currentClientTab: StateFlow<ClientTab> = _currentClientTab.asStateFlow()

    fun selectClientTab(tab: ClientTab) {
        _currentClientTab.value = tab
    }

    init {
        val db = SalonDatabase.getDatabase(application, viewModelScope)
        repository = SalonRepository(db.salonDao())

        viewModelScope.launch {
            clientAuthUser.collect { user ->
                if (user != null) {
                    if (user.email.trim().equals(ADMIN_EMAIL, ignoreCase = true)) {
                        _currentRole.value = AppRole.ADMIN
                    } else {
                        _currentRole.value = AppRole.CLIENT
                    }
                } else {
                    _currentRole.value = AppRole.CLIENT
                }
            }
        }
    }

    // Appointments & Filter
    val allAppointments: StateFlow<List<Appointment>> = repository.appointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _appointmentDateFilter = MutableStateFlow("HOJE") // HOJE, AMANHA, SEMANA, TODOS
    val appointmentDateFilter: StateFlow<String> = _appointmentDateFilter.asStateFlow()

    val filteredAppointments: StateFlow<List<Appointment>> = combine(
        allAppointments,
        _appointmentDateFilter
    ) { appointments, filter ->
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val todayStr = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = dateFormat.format(cal.time)

        when (filter) {
            "HOJE" -> appointments.filter { it.appointmentDate == todayStr }
            "AMANHA" -> appointments.filter { it.appointmentDate == tomorrowStr }
            "SEMANA" -> {
                // Next 7 days
                val limitCal = Calendar.getInstance()
                limitCal.add(Calendar.DAY_OF_YEAR, 7)
                val limitStr = dateFormat.format(limitCal.time)
                appointments.filter { it.appointmentDate in todayStr..limitStr }
            }
            else -> appointments
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Transactions & Filter
    val allTransactions: StateFlow<List<FinancialTransaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _financeMonthFilter = MutableStateFlow("ESTE_MES") // ESTE_MES, MES_ANTERIOR, TODOS
    val financeMonthFilter: StateFlow<String> = _financeMonthFilter.asStateFlow()

    val filteredTransactions: StateFlow<List<FinancialTransaction>> = combine(
        allTransactions,
        _financeMonthFilter
    ) { transactions, filter ->
        val cal = Calendar.getInstance()
        val currentMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)

        cal.add(Calendar.MONTH, -1)
        val prevMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)

        when (filter) {
            "ESTE_MES" -> transactions.filter { it.date.startsWith(currentMonthPrefix) }
            "MES_ANTERIOR" -> transactions.filter { it.date.startsWith(prevMonthPrefix) }
            else -> transactions
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Professionals, Services, Loyalty
    val professionals: StateFlow<List<Professional>> = repository.professionals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val services: StateFlow<List<SalonService>> = repository.services
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loyaltyClients: StateFlow<List<ClientLoyalty>> = repository.loyaltyClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<LoyaltyReward>> = repository.rewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Inventory & Stock ---
    val allProducts: StateFlow<List<Product>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStockMovements: StateFlow<List<StockMovement>> = repository.stockMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Customer Feedback & Ratings ---
    val allFeedbacks: StateFlow<List<CustomerFeedback>> = repository.feedbacks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Service Filters
    private val _selectedServiceCategory = MutableStateFlow("TODOS")
    val selectedServiceCategory: StateFlow<String> = _selectedServiceCategory.asStateFlow()

    val filteredServices: StateFlow<List<SalonService>> = combine(
        services,
        _selectedServiceCategory
    ) { all, category ->
        if (category == "TODOS") all else all.filter { it.category.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Inventory Filters
    private val _selectedProductCategory = MutableStateFlow("TODOS")
    val selectedProductCategory: StateFlow<String> = _selectedProductCategory.asStateFlow()

    private val _inventoryStockFilter = MutableStateFlow("TODOS") // TODOS, BAIXO_ESTOQUE
    val inventoryStockFilter: StateFlow<String> = _inventoryStockFilter.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _selectedProductCategory,
        _inventoryStockFilter
    ) { products, cat, stockFilter ->
        products.filter { prod ->
            val matchesCat = (cat == "TODOS" || prod.category.equals(cat, ignoreCase = true))
            val matchesStock = (stockFilter == "TODOS" || (stockFilter == "BAIXO_ESTOQUE" && prod.isLowStock))
            matchesCat && matchesStock
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockCount: StateFlow<Int> = allProducts.map { list ->
        list.count { it.isLowStock }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalInventoryValue: StateFlow<Double> = allProducts.map { list ->
        list.sumOf { it.costPrice * it.currentStock }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Feedback Ratings Map (professionalId -> Pair<AverageRating, Count>)
    val professionalRatings: StateFlow<Map<Long, Pair<Double, Int>>> = allFeedbacks.map { feedbacks ->
        feedbacks.groupBy { it.professionalId }.mapValues { (_, reviews) ->
            val avg = reviews.map { it.rating }.average()
            Pair(if (avg.isNaN()) 5.0 else avg, reviews.size)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val overallSalonRating: StateFlow<Double> = allFeedbacks.map { list ->
        if (list.isEmpty()) 5.0 else list.map { it.rating }.average()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5.0)

    // UI Dialog & Navigation States
    val isNewAppointmentDialogVisible = MutableStateFlow(false)
    val preSelectedServiceForAppointment = MutableStateFlow<SalonService?>(null)
    val isNewTransactionDialogVisible = MutableStateFlow(false)
    val isProfessionalDialogVisible = MutableStateFlow(false)
    val editingProfessional = MutableStateFlow<Professional?>(null)

    // Service Dialog States
    val isServiceDialogVisible = MutableStateFlow(false)
    val editingService = MutableStateFlow<SalonService?>(null)

    // Product Dialog States
    val isProductDialogVisible = MutableStateFlow(false)
    val editingProduct = MutableStateFlow<Product?>(null)

    // Stock Movement Dialog States
    val isStockMovementDialogVisible = MutableStateFlow(false)
    val selectedProductForMovement = MutableStateFlow<Product?>(null)

    // Customer Feedback Dialog States
    val isFeedbackDialogVisible = MutableStateFlow(false)
    val selectedAppointmentForFeedback = MutableStateFlow<Appointment?>(null)
    val targetProfessionalForFeedback = MutableStateFlow<Professional?>(null)
    val selectedProfessionalForFeedback = targetProfessionalForFeedback

    // Sub-tab switchers
    val managementSubTab = MutableStateFlow("FINANCEIRO") // FINANCEIRO, RELATORIOS, FIDELIDADE_NUVEM
    val teamSubTab = MutableStateFlow("EQUIPE") // EQUIPE, AVALIACOES

    // Online Payment Sheet / Dialog State
    val selectedAppointmentForPayment = MutableStateFlow<Appointment?>(null)

    // ==========================================
    // CLIENT VERSION STATE & CART
    // ==========================================
    val activeClientPhone = MutableStateFlow("11991234567") // Default to "Fernanda Silva"
    val activeClient: StateFlow<ClientLoyalty> = combine(
        loyaltyClients,
        activeClientPhone
    ) { clients, phone ->
        clients.firstOrNull { it.clientPhone == phone } ?: clients.firstOrNull() ?: ClientLoyalty(
            id = 1,
            clientName = "Fernanda Silva",
            clientPhone = "11991234567",
            totalPoints = 145,
            tier = "OURO",
            totalVisits = 8,
            totalSpent = 1420.0
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ClientLoyalty(
            id = 1,
            clientName = "Fernanda Silva",
            clientPhone = "11991234567",
            totalPoints = 145,
            tier = "OURO",
            totalVisits = 8,
            totalSpent = 1420.0
        )
    )

    val clientAppointments: StateFlow<List<Appointment>> = combine(
        allAppointments,
        activeClient
    ) { appointments, client ->
        appointments.filter {
            it.clientPhone.trim() == client.clientPhone.trim() ||
            it.clientName.equals(client.clientName.trim(), ignoreCase = true)
        }.sortedWith(compareByDescending<Appointment> { it.appointmentDate }.thenByDescending { it.appointmentTime })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart for product purchases
    private val _cartItems = MutableStateFlow<Map<Long, CartItem>>(emptyMap())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartTotal: StateFlow<Double> = cartItems.map { list ->
        list.sumOf { it.product.salePrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount: StateFlow<Int> = cartItems.map { list ->
        list.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isCartSheetVisible = MutableStateFlow(false)
    val isClientBookingDialogVisible = MutableStateFlow(false)
    val selectedServiceForClientBooking = MutableStateFlow<SalonService?>(null)

    // Cloud Backup State
    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _lastCloudBackupTime = MutableStateFlow(CloudBackupManager.getLastBackupTime(application))
    val lastCloudBackupTime: StateFlow<Long> = _lastCloudBackupTime.asStateFlow()

    private val _isAutoSyncEnabled = MutableStateFlow(CloudBackupManager.isAutoSyncEnabled(application))
    val isAutoSyncEnabled: StateFlow<Boolean> = _isAutoSyncEnabled.asStateFlow()

    val cloudVaultId = CloudBackupManager.getCloudVaultId(application)

    // Toast/Snackbar Message Event
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage = _userMessage.asSharedFlow()

    // Monthly Report Data
    val monthlyReport: StateFlow<MonthlyReportData> = combine(
        allTransactions,
        allAppointments,
        professionals,
        loyaltyClients
    ) { transactions, appointments, profs, clients ->
        calculateMonthlyReport(transactions, appointments, profs, clients)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MonthlyReportData(
            monthName = "Mês Atual",
            totalRevenue = 0.0,
            totalExpense = 0.0,
            netProfit = 0.0,
            profitMarginPercent = 0.0,
            completedAppointmentsCount = 0,
            averageTicket = 0.0,
            retentionRatePercent = 0.0,
            topServices = emptyList(),
            professionalStats = emptyList()
        )
    )

    fun selectTab(tab: SalonTab) {
        _currentTab.value = tab
    }

    fun setAppointmentDateFilter(filter: String) {
        _appointmentDateFilter.value = filter
    }

    fun setFinanceMonthFilter(filter: String) {
        _financeMonthFilter.value = filter
    }

    fun confirmAppointment(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.confirmAppointment(id)
            _userMessage.emit("Horário confirmado com sucesso!")
        }
    }

    fun completeAppointment(appointment: Appointment, paymentMethod: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.completeAppointment(appointment, paymentMethod)
            _userMessage.emit("Atendimento concluído! Receita registrada e pontos de fidelidade creditados.")
        }
    }

    fun cancelAppointment(appointment: Appointment) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAppointment(appointment.copy(status = "CANCELADO"))
            _userMessage.emit("Agendamento cancelado.")
        }
    }

    fun openPaymentModal(appointment: Appointment) {
        selectedAppointmentForPayment.value = appointment
    }

    fun closePaymentModal() {
        selectedAppointmentForPayment.value = null
    }

    fun confirmOnlinePayment(appointmentId: Long, method: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.processOnlinePayment(appointmentId, method)
            selectedAppointmentForPayment.value = null
            _userMessage.emit("Pagamento online confirmado com sucesso!")
        }
    }

    fun addAppointment(
        clientName: String,
        clientPhone: String,
        service: SalonService,
        professional: Professional,
        date: String,
        time: String,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val appointment = Appointment(
                clientName = clientName.trim(),
                clientPhone = clientPhone.trim(),
                serviceId = service.id,
                serviceName = service.name,
                servicePrice = service.price,
                professionalId = professional.id,
                professionalName = professional.name,
                appointmentDate = date,
                appointmentTime = time,
                notes = notes.trim()
            )
            repository.insertAppointment(appointment)
            isNewAppointmentDialogVisible.value = false
            _userMessage.emit("Agendamento criado com sucesso!")
        }
    }

    fun addTransaction(
        description: String,
        amount: Double,
        type: String,
        category: String,
        paymentMethod: String,
        date: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val tx = FinancialTransaction(
                description = description.trim(),
                amount = amount,
                type = type,
                category = category,
                paymentMethod = paymentMethod,
                date = date
            )
            repository.insertTransaction(tx)
            isNewTransactionDialogVisible.value = false
            _userMessage.emit("Lançamento financeiro registrado com sucesso!")
        }
    }

    fun deleteTransaction(tx: FinancialTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(tx)
            _userMessage.emit("Lançamento excluído.")
        }
    }

    fun saveProfessional(
        id: Long,
        name: String,
        role: String,
        phone: String,
        commission: Int,
        workingDays: String,
        workingHours: String,
        colorHex: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (id == 0L) {
                repository.insertProfessional(
                    Professional(
                        name = name.trim(),
                        role = role.trim(),
                        phone = phone.trim(),
                        commissionPercent = commission,
                        workingDays = workingDays,
                        workingHours = workingHours,
                        colorHex = colorHex
                    )
                )
                _userMessage.emit("Profissional cadastrado com sucesso!")
            } else {
                val current = repository.professionals.first().find { it.id == id }
                if (current != null) {
                    repository.updateProfessional(
                        current.copy(
                            name = name.trim(),
                            role = role.trim(),
                            phone = phone.trim(),
                            commissionPercent = commission,
                            workingDays = workingDays,
                            workingHours = workingHours,
                            colorHex = colorHex
                        )
                    )
                    _userMessage.emit("Dados do profissional atualizados!")
                }
            }
            isProfessionalDialogVisible.value = false
            editingProfessional.value = null
        }
    }

    fun toggleProfessionalActive(professional: Professional) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfessional(professional.copy(active = !professional.active))
            _userMessage.emit("${professional.name} marcado como ${if (!professional.active) "ativo" else "inativo"}.")
        }
    }

    fun redeemReward(clientPhone: String, reward: LoyaltyReward) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.redeemReward(clientPhone, reward)
            if (success) {
                _userMessage.emit("Recompensa resgatada! Desconto de R$ ${String.format(Locale.getDefault(), "%.2f", reward.discountValue)} aplicado.")
            } else {
                _userMessage.emit("Pontos insuficientes para resgatar esta recompensa.")
            }
        }
    }

    fun toggleAutoSync(enabled: Boolean) {
        _isAutoSyncEnabled.value = enabled
        CloudBackupManager.setAutoSyncEnabled(getApplication(), enabled)
    }

    fun syncCloudBackupNow() {
        viewModelScope.launch(Dispatchers.IO) {
            _isCloudSyncing.value = true
            delay(1200) // Realistic cloud round-trip
            try {
                val bundle = repository.createCloudBackupBundle()
                val json = CloudBackupManager.serializeBackup(bundle)
                CloudBackupManager.saveCloudSnapshot(getApplication(), json)
                _lastCloudBackupTime.value = System.currentTimeMillis()
                _userMessage.emit("Backup em nuvem sincronizado e criptografado com sucesso!")
            } catch (e: Exception) {
                _userMessage.emit("Erro ao realizar backup: ${e.message}")
            } finally {
                _isCloudSyncing.value = false
            }
        }
    }

    fun restoreFromCloudBackup() {
        viewModelScope.launch(Dispatchers.IO) {
            _isCloudSyncing.value = true
            delay(1000)
            try {
                val cachedJson = CloudBackupManager.getCloudSnapshot(getApplication())
                if (cachedJson != null) {
                    val bundle = CloudBackupManager.deserializeBackup(cachedJson)
                    repository.restoreFromBundle(bundle)
                    _userMessage.emit("Dados restaurados da nuvem com sucesso!")
                } else {
                    // Fallback to fresh cloud bundle from initial state
                    val bundle = repository.createCloudBackupBundle()
                    val json = CloudBackupManager.serializeBackup(bundle)
                    CloudBackupManager.saveCloudSnapshot(getApplication(), json)
                    _userMessage.emit("Nenhum backup anterior encontrado. Novo ponto seguro criado.")
                }
            } catch (e: Exception) {
                _userMessage.emit("Erro ao restaurar dados da nuvem: ${e.message}")
            } finally {
                _isCloudSyncing.value = false
            }
        }
    }

    fun exportBackupFile() {
        viewModelScope.launch(Dispatchers.IO) {
            val bundle = repository.createCloudBackupBundle()
            val json = CloudBackupManager.serializeBackup(bundle)
            CloudBackupManager.shareBackupFile(getApplication(), json)
        }
    }

    private fun calculateMonthlyReport(
        transactions: List<FinancialTransaction>,
        appointments: List<Appointment>,
        profs: List<Professional>,
        clients: List<ClientLoyalty>
    ): MonthlyReportData {
        val now = Calendar.getInstance()
        val currentMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(now.time)
        val monthDisplay = SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR")).format(now.time)
            .replaceFirstChar { it.uppercase() }

        val monthlyTxs = transactions.filter { it.date.startsWith(currentMonthPrefix) }
        val totalRevenue = monthlyTxs.filter { it.type == "RECEITA" }.sumOf { it.amount }
        val totalExpense = monthlyTxs.filter { it.type == "DESPESA" }.sumOf { it.amount }
        val netProfit = totalRevenue - totalExpense
        val profitMargin = if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0

        val monthlyApts = appointments.filter { it.appointmentDate.startsWith(currentMonthPrefix) }
        val completedApts = monthlyApts.filter { it.status == "CONCLUIDO" || it.paymentStatus.startsWith("PAGO") }
        val completedCount = completedApts.size
        val averageTicket = if (completedCount > 0) totalRevenue / completedCount else if (totalRevenue > 0) totalRevenue else 0.0

        // Retention Rate
        val returningClients = clients.filter { it.totalVisits > 1 }.size
        val totalClients = clients.size.coerceAtLeast(1)
        val retentionRate = (returningClients.toDouble() / totalClients.toDouble()) * 100.0

        // Top Services by revenue
        val serviceRevenueMap = mutableMapOf<String, Double>()
        for (apt in completedApts) {
            val cur = serviceRevenueMap.getOrDefault(apt.serviceName, 0.0)
            serviceRevenueMap[apt.serviceName] = cur + apt.servicePrice
        }
        val topServices = serviceRevenueMap.toList()
            .sortedByDescending { it.second }
            .take(5)

        // Professional performance
        val profStats = profs.map { prof ->
            val profApts = completedApts.filter { it.professionalId == prof.id || it.professionalName == prof.name }
            val count = profApts.size
            val billed = profApts.sumOf { it.servicePrice }
            val commissionVal = billed * (prof.commissionPercent / 100.0)
            ProfessionalPerformance(
                professionalName = prof.name,
                appointmentCount = count,
                totalBilled = billed,
                commissionPercent = prof.commissionPercent,
                commissionAmount = commissionVal
            )
        }

        return MonthlyReportData(
            monthName = monthDisplay,
            totalRevenue = totalRevenue,
            totalExpense = totalExpense,
            netProfit = netProfit,
            profitMarginPercent = profitMargin,
            completedAppointmentsCount = completedCount,
            averageTicket = averageTicket,
            retentionRatePercent = retentionRate,
            topServices = topServices,
            professionalStats = profStats
        )
    }

    fun generateReportShareText(): String {
        val report = monthlyReport.value
        val sb = StringBuilder()
        sb.append("📊 *RELATÓRIO MENSAL DE DESEMPENHO - SALÃO GESTÃO* 📊\n")
        sb.append("🗓️ Período: *${report.monthName}*\n\n")

        sb.append("💰 *RESUMO FINANCEIRO:*\n")
        sb.append("• Faturamento Total: *R$ ${String.format(Locale.getDefault(), "%.2f", report.totalRevenue)}*\n")
        sb.append("• Despesas Operacionais: *R$ ${String.format(Locale.getDefault(), "%.2f", report.totalExpense)}*\n")
        sb.append("• Lucro Líquido Real: *R$ ${String.format(Locale.getDefault(), "%.2f", report.netProfit)}*\n")
        sb.append("• Margem de Lucro: *${String.format(Locale.getDefault(), "%.1f", report.profitMarginPercent)}%*\n\n")

        sb.append("📈 *MÉTRICAS DE CLIENTES & RETENÇÃO:*\n")
        sb.append("• Atendimentos Realizados: *${report.completedAppointmentsCount}*\n")
        sb.append("• Ticket Médio por Cliente: *R$ ${String.format(Locale.getDefault(), "%.2f", report.averageTicket)}*\n")
        sb.append("• Taxa de Retenção de Clientes: *${String.format(Locale.getDefault(), "%.1f", report.retentionRatePercent)}%*\n\n")

        if (report.topServices.isNotEmpty()) {
            sb.append("💇 *TOP SERVIÇOS MAIS RENTÁVEIS:*\n")
            report.topServices.forEachIndexed { index, pair ->
                sb.append("${index + 1}. ${pair.first}: R$ ${String.format(Locale.getDefault(), "%.2f", pair.second)}\n")
            }
            sb.append("\n")
        }

        if (report.professionalStats.isNotEmpty()) {
            sb.append("👥 *DESEMPENHO DA EQUIPE:*\n")
            report.professionalStats.forEach { p ->
                sb.append("• *${p.professionalName}*: ${p.appointmentCount} atendimentos | R$ ${String.format(Locale.getDefault(), "%.2f", p.totalBilled)} faturados (Comissão: R$ ${String.format(Locale.getDefault(), "%.2f", p.commissionAmount)})\n")
            }
            sb.append("\n")
        }

        sb.append("🔒 *Segurança:* Dados sincronizados e protegidos em Nuvem Criptografada.\n")
        sb.append("Gerado automaticamente pelo aplicativo *Salão Gestão*.")
        return sb.toString()
    }

    // ==========================================
    // SERVICE CATALOG MANAGEMENT
    // ==========================================
    fun setServiceCategory(category: String) {
        _selectedServiceCategory.value = category
    }

    fun openNewServiceDialog() {
        editingService.value = null
        isServiceDialogVisible.value = true
    }

    fun openEditServiceDialog(service: SalonService) {
        editingService.value = service
        isServiceDialogVisible.value = true
    }

    fun closeServiceDialog() {
        isServiceDialogVisible.value = false
        editingService.value = null
    }

    fun saveService(
        name: String,
        category: String,
        price: Double,
        durationMinutes: Int,
        points: Int,
        description: String,
        iconType: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = editingService.value
            val item = if (current != null) {
                current.copy(
                    name = name.trim(),
                    category = category.trim(),
                    price = price,
                    durationMinutes = durationMinutes,
                    loyaltyPointsEarned = points,
                    description = description.trim(),
                    iconType = iconType
                )
            } else {
                SalonService(
                    name = name.trim(),
                    category = category.trim(),
                    price = price,
                    durationMinutes = durationMinutes,
                    loyaltyPointsEarned = points,
                    description = description.trim(),
                    iconType = iconType
                )
            }
            if (current != null) {
                repository.updateService(item)
            } else {
                repository.insertService(item)
            }
            closeServiceDialog()
            _userMessage.emit("Serviço salvo no catálogo!")
        }
    }

    fun deleteService(service: SalonService) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteService(service)
            _userMessage.emit("Serviço excluído do catálogo.")
        }
    }

    // ==========================================
    // INVENTORY MANAGEMENT
    // ==========================================
    fun setProductCategory(category: String) {
        _selectedProductCategory.value = category
    }

    fun setInventoryStockFilter(filter: String) {
        _inventoryStockFilter.value = filter
    }

    fun openNewProductDialog() {
        editingProduct.value = null
        isProductDialogVisible.value = true
    }

    fun openEditProductDialog(product: Product) {
        editingProduct.value = product
        isProductDialogVisible.value = true
    }

    fun closeProductDialog() {
        isProductDialogVisible.value = false
        editingProduct.value = null
    }

    fun saveProduct(
        name: String,
        category: String,
        sku: String,
        currentStock: Int,
        minStockAlert: Int,
        costPrice: Double,
        salePrice: Double,
        unit: String,
        supplier: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = editingProduct.value
            val item = if (current != null) {
                current.copy(
                    name = name.trim(),
                    category = category.trim(),
                    sku = sku.trim(),
                    currentStock = currentStock,
                    minStockAlert = minStockAlert,
                    costPrice = costPrice,
                    salePrice = salePrice,
                    unit = unit.trim(),
                    supplier = supplier.trim(),
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                Product(
                    name = name.trim(),
                    category = category.trim(),
                    sku = sku.trim(),
                    currentStock = currentStock,
                    minStockAlert = minStockAlert,
                    costPrice = costPrice,
                    salePrice = salePrice,
                    unit = unit.trim(),
                    supplier = supplier.trim()
                )
            }
            if (current != null) {
                repository.updateProduct(item)
            } else {
                repository.insertProduct(item)
            }
            closeProductDialog()
            _userMessage.emit("Produto cadastrado no estoque!")
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(product)
            _userMessage.emit("Produto excluído do estoque.")
        }
    }

    fun openStockMovementDialog(product: Product) {
        selectedProductForMovement.value = product
        isStockMovementDialogVisible.value = true
    }

    fun closeStockMovementDialog() {
        isStockMovementDialogVisible.value = false
        selectedProductForMovement.value = null
    }

    fun recordStockMovement(
        productId: Long,
        type: String, // "ENTRADA" or "SAIDA"
        quantity: Int,
        reason: String,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.recordStockMovement(productId, type, quantity, reason, notes)
            if (success) {
                closeStockMovementDialog()
                val actionVerb = if (type == "ENTRADA") "Entrada de" else "Saída de"
                _userMessage.emit("$actionVerb $quantity item(ns) registrada!")
            }
        }
    }

    // ==========================================
    // CUSTOMER FEEDBACK & RATINGS
    // ==========================================
    fun openFeedbackDialog(appointment: Appointment? = null, professional: Professional? = null) {
        selectedAppointmentForFeedback.value = appointment
        targetProfessionalForFeedback.value = professional
        isFeedbackDialogVisible.value = true
    }

    fun openFeedbackDialogForAppointment(appointment: Appointment) {
        selectedAppointmentForFeedback.value = appointment
        targetProfessionalForFeedback.value = null
        isFeedbackDialogVisible.value = true
    }

    fun openFeedbackDialogForProfessional(professional: Professional) {
        selectedAppointmentForFeedback.value = null
        targetProfessionalForFeedback.value = professional
        isFeedbackDialogVisible.value = true
    }

    fun closeFeedbackDialog() {
        isFeedbackDialogVisible.value = false
        selectedAppointmentForFeedback.value = null
        targetProfessionalForFeedback.value = null
    }

    fun submitFeedback(
        appointmentId: Long,
        clientName: String,
        clientPhone: String,
        professionalId: Long,
        professionalName: String,
        serviceId: Long,
        serviceName: String,
        rating: Int,
        comment: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val feedback = CustomerFeedback(
                appointmentId = appointmentId,
                clientName = clientName.trim(),
                clientPhone = clientPhone.trim(),
                professionalId = professionalId,
                professionalName = professionalName,
                serviceId = serviceId,
                serviceName = serviceName,
                rating = rating.coerceIn(1, 5),
                comment = comment.trim(),
                date = todayStr
            )
            repository.insertFeedback(feedback)
            closeFeedbackDialog()
            _userMessage.emit("Avaliação de $rating estrelas registrada com sucesso!")
        }
    }

    // ==========================================
    // CLIENT VERSION ACTIONS
    // ==========================================
    fun switchActiveClient(phone: String) {
        activeClientPhone.value = phone
    }

    fun loginClient(email: String, pass: String) {
        val cleanEmail = email.trim()
        val isAdmin = cleanEmail.equals(ADMIN_EMAIL, ignoreCase = true)

        viewModelScope.launch {
            _isClientAuthLoading.value = true
            _clientAuthErrorMessage.value = null

            val result = firebaseAuthService.signInWithEmail(cleanEmail, pass)
            _isClientAuthLoading.value = false

            result.onSuccess { user ->
                if (isAdmin || user.email.trim().equals(ADMIN_EMAIL, ignoreCase = true)) {
                    _currentRole.value = AppRole.ADMIN
                    _userMessage.emit("Bem-vinda, Administradora Laura Ivini!")
                } else {
                    _currentRole.value = AppRole.CLIENT
                    _userMessage.emit("Login realizado com sucesso! Bem-vindo(a), ${user.displayName}.")
                    val clients = repository.loyaltyClients.first()
                    val match = if (user.phoneNumber.isNotEmpty()) {
                        clients.firstOrNull { it.clientPhone == user.phoneNumber }
                    } else {
                        clients.firstOrNull { it.clientName.equals(user.displayName, ignoreCase = true) }
                    }
                    if (match != null) {
                        activeClientPhone.value = match.clientPhone
                    }
                }
            }.onFailure { ex ->
                _clientAuthErrorMessage.value = ex.message
            }
        }
    }

    fun loginAdminDirect(pass: String = "laura123") {
        loginClient(ADMIN_EMAIL, pass)
    }

    fun registerClient(name: String, phone: String, email: String, pass: String) {
        viewModelScope.launch {
            _isClientAuthLoading.value = true
            _clientAuthErrorMessage.value = null
            val result = firebaseAuthService.signUpWithEmail(name, phone, email, pass)
            _isClientAuthLoading.value = false
            result.onSuccess { user ->
                val clients = repository.loyaltyClients.first()
                val cleanPhone = phone.trim().ifEmpty { "11999990000" }
                val existing = clients.firstOrNull { it.clientPhone == cleanPhone }
                if (existing == null) {
                    repository.insertLoyaltyClient(
                        ClientLoyalty(
                            clientName = name.trim(),
                            clientPhone = cleanPhone,
                            totalPoints = 50, // Bônus de boas-vindas
                            tier = "BRONZE",
                            totalVisits = 0,
                            lastVisitDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            totalSpent = 0.0
                        )
                    )
                }
                activeClientPhone.value = cleanPhone
                _userMessage.emit("Conta criada com sucesso! 50 pontos de boas-vindas adicionados.")
            }.onFailure { ex ->
                _clientAuthErrorMessage.value = ex.message
            }
        }
    }

    fun loginClientAnonymously() {
        viewModelScope.launch {
            _isClientAuthLoading.value = true
            _clientAuthErrorMessage.value = null
            val result = firebaseAuthService.signInAnonymously()
            _isClientAuthLoading.value = false
            result.onSuccess {
                _userMessage.emit("Acesso anônimo iniciado com sucesso.")
            }.onFailure { ex ->
                _clientAuthErrorMessage.value = ex.message
            }
        }
    }

    fun resetClientPassword(email: String) {
        viewModelScope.launch {
            _isClientAuthLoading.value = true
            _clientAuthErrorMessage.value = null
            val result = firebaseAuthService.sendPasswordReset(email)
            _isClientAuthLoading.value = false
            result.onSuccess {
                _userMessage.emit("Link de redefinição enviado para $email com sucesso!")
            }.onFailure { ex ->
                _clientAuthErrorMessage.value = ex.message
            }
        }
    }

    fun logoutClient() {
        firebaseAuthService.signOut()
        _currentRole.value = AppRole.CLIENT
        viewModelScope.launch {
            _userMessage.emit("Sessão encerrada com sucesso.")
        }
    }

    fun selectTestPersonaForAuth(persona: ClientLoyalty) {
        firebaseAuthService.setLocalTestUser(
            name = persona.clientName,
            phone = persona.clientPhone,
            email = "${persona.clientName.lowercase().replace(" ", ".")}@gmail.com"
        )
        activeClientPhone.value = persona.clientPhone
        viewModelScope.launch {
            _userMessage.emit("Conectado como ${persona.clientName} (Demonstração)")
        }
    }

    fun updateClientProfile(name: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.loyaltyClients.first().firstOrNull { it.clientPhone == phone }
            if (existing != null) {
                repository.insertLoyaltyClient(existing.copy(clientName = name.trim()))
            } else {
                repository.insertLoyaltyClient(
                    ClientLoyalty(
                        clientName = name.trim(),
                        clientPhone = phone.trim(),
                        totalPoints = 0,
                        tier = "BRONZE",
                        totalVisits = 0,
                        lastVisitDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        totalSpent = 0.0
                    )
                )
            }
            activeClientPhone.value = phone.trim()
            _userMessage.emit("Perfil atualizado com sucesso!")
        }
    }

    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[product.id]
        if (existing != null) {
            val newQty = (existing.quantity + 1).coerceAtMost(product.currentStock)
            current[product.id] = existing.copy(quantity = newQty)
        } else {
            current[product.id] = CartItem(product = product, quantity = 1)
        }
        _cartItems.value = current
        viewModelScope.launch {
            _userMessage.emit("${product.name} adicionado à sacola!")
        }
    }

    fun updateCartQuantity(productId: Long, quantity: Int) {
        val current = _cartItems.value.toMutableMap()
        if (quantity <= 0) {
            current.remove(productId)
        } else {
            val item = current[productId]
            if (item != null) {
                val safeQty = quantity.coerceAtMost(item.product.currentStock)
                current[productId] = item.copy(quantity = safeQty)
            }
        }
        _cartItems.value = current
    }

    fun removeFromCart(productId: Long) {
        val current = _cartItems.value.toMutableMap()
        current.remove(productId)
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    fun checkoutCart(paymentMethod: String, isPickup: Boolean, addressOrNotes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = activeClient.value
            val items = cartItems.value.map { Pair(it.product, it.quantity) }
            if (items.isEmpty()) return@launch

            val success = repository.processProductPurchase(
                clientName = client.clientName,
                clientPhone = client.clientPhone,
                items = items,
                paymentMethod = paymentMethod,
                isPickup = isPickup,
                notes = addressOrNotes
            )
            if (success) {
                clearCart()
                isCartSheetVisible.value = false
                val pointsGained = (items.sumOf { it.first.salePrice * it.second } / 10).toInt().coerceAtLeast(1)
                _userMessage.emit("Pedido confirmado! Você ganhou +$pointsGained pontos!")
            }
        }
    }

    fun openClientBooking(service: SalonService? = null) {
        selectedServiceForClientBooking.value = service
        isClientBookingDialogVisible.value = true
    }

    fun closeClientBooking() {
        isClientBookingDialogVisible.value = false
        selectedServiceForClientBooking.value = null
    }

    fun clientBookAppointment(
        service: SalonService,
        professional: Professional,
        date: String,
        time: String,
        notes: String,
        paymentMethod: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = activeClient.value
            val initialStatus = if (paymentMethod.startsWith("PAGO")) "CONFIRMADO" else "AGENDADO"
            val paymentStatus = when (paymentMethod) {
                "PIX" -> "PAGO_PIX"
                "CARTAO" -> "PAGO_CARTAO"
                else -> "PENDENTE"
            }

            val appointment = Appointment(
                clientName = client.clientName,
                clientPhone = client.clientPhone,
                serviceId = service.id,
                serviceName = service.name,
                servicePrice = service.price,
                professionalId = professional.id,
                professionalName = professional.name,
                appointmentDate = date,
                appointmentTime = time,
                status = initialStatus,
                paymentStatus = paymentStatus,
                notes = notes
            )
            val newId = repository.insertAppointment(appointment)

            if (paymentStatus.startsWith("PAGO_")) {
                repository.processOnlinePayment(newId, paymentMethod)
            }

            closeClientBooking()
            _userMessage.emit("Agendamento solicitado com sucesso para $date às $time!")
        }
    }

    fun redeemReward(reward: LoyaltyReward) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = activeClient.value
            val success = repository.redeemLoyaltyReward(client.clientPhone, reward)
            if (success) {
                _userMessage.emit("Recompensa resgatada com sucesso! Código gerado.")
            } else {
                _userMessage.emit("Pontos insuficientes para resgatar esta recompensa.")
            }
        }
    }
}
