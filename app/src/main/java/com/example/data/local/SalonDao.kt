package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Appointment
import com.example.data.model.ClientLoyalty
import com.example.data.model.CustomerFeedback
import com.example.data.model.FinancialTransaction
import com.example.data.model.LoyaltyReward
import com.example.data.model.Product
import com.example.data.model.Professional
import com.example.data.model.SalonService
import com.example.data.model.StockMovement
import kotlinx.coroutines.flow.Flow

@Dao
interface SalonDao {

    // --- Appointments ---
    @Query("SELECT * FROM appointments ORDER BY appointmentDate ASC, appointmentTime ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE appointmentDate = :date ORDER BY appointmentTime ASC")
    fun getAppointmentsForDate(date: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun getAppointmentById(id: Long): Appointment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAppointments(appointments: List<Appointment>)

    @Query("DELETE FROM appointments")
    suspend fun clearAllAppointments()

    // --- Transactions ---
    @Query("SELECT * FROM transactions ORDER BY date DESC, timestamp DESC")
    fun getAllTransactions(): Flow<List<FinancialTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinancialTransaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: FinancialTransaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<FinancialTransaction>)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    // --- Professionals ---
    @Query("SELECT * FROM professionals ORDER BY name ASC")
    fun getAllProfessionals(): Flow<List<Professional>>

    @Query("SELECT * FROM professionals WHERE active = 1 ORDER BY name ASC")
    fun getActiveProfessionals(): Flow<List<Professional>>

    @Query("SELECT * FROM professionals WHERE id = :id LIMIT 1")
    suspend fun getProfessionalById(id: Long): Professional?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfessional(professional: Professional): Long

    @Update
    suspend fun updateProfessional(professional: Professional)

    @Delete
    suspend fun deleteProfessional(professional: Professional)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProfessionals(professionals: List<Professional>)

    @Query("DELETE FROM professionals")
    suspend fun clearAllProfessionals()

    // --- Services ---
    @Query("SELECT * FROM services ORDER BY category ASC, name ASC")
    fun getAllServices(): Flow<List<SalonService>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: SalonService): Long

    @Update
    suspend fun updateService(service: SalonService)

    @Delete
    suspend fun deleteService(service: SalonService)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllServices(services: List<SalonService>)

    @Query("DELETE FROM services")
    suspend fun clearAllServices()

    // --- Loyalty ---
    @Query("SELECT * FROM client_loyalty ORDER BY totalPoints DESC")
    fun getAllLoyaltyClients(): Flow<List<ClientLoyalty>>

    @Query("SELECT * FROM client_loyalty WHERE clientPhone = :phone LIMIT 1")
    suspend fun getClientLoyaltyByPhone(phone: String): ClientLoyalty?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLoyalty(loyalty: ClientLoyalty): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLoyaltyClients(clients: List<ClientLoyalty>)

    @Query("DELETE FROM client_loyalty")
    suspend fun clearAllLoyaltyClients()

    // --- Rewards ---
    @Query("SELECT * FROM loyalty_rewards ORDER BY pointsRequired ASC")
    fun getAllRewards(): Flow<List<LoyaltyReward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: LoyaltyReward): Long

    @Delete
    suspend fun deleteReward(reward: LoyaltyReward)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRewards(rewards: List<LoyaltyReward>)

    @Query("DELETE FROM loyalty_rewards")
    suspend fun clearAllRewards()

    // --- Inventory Products ---
    @Query("SELECT * FROM inventory_products ORDER BY category ASC, name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM inventory_products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<Product>)

    @Query("DELETE FROM inventory_products")
    suspend fun clearAllProducts()

    // --- Stock Movements ---
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC")
    fun getAllStockMovements(): Flow<List<StockMovement>>

    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY timestamp DESC")
    fun getMovementsForProduct(productId: Long): Flow<List<StockMovement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovement(movement: StockMovement): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStockMovements(movements: List<StockMovement>)

    @Query("DELETE FROM stock_movements")
    suspend fun clearAllStockMovements()

    // --- Customer Feedback & Ratings ---
    @Query("SELECT * FROM customer_feedbacks ORDER BY timestamp DESC")
    fun getAllFeedbacks(): Flow<List<CustomerFeedback>>

    @Query("SELECT * FROM customer_feedbacks WHERE professionalId = :professionalId ORDER BY timestamp DESC")
    fun getFeedbacksForProfessional(professionalId: Long): Flow<List<CustomerFeedback>>

    @Query("SELECT * FROM customer_feedbacks WHERE serviceId = :serviceId ORDER BY timestamp DESC")
    fun getFeedbacksForService(serviceId: Long): Flow<List<CustomerFeedback>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: CustomerFeedback): Long

    @Delete
    suspend fun deleteFeedback(feedback: CustomerFeedback)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFeedbacks(feedbacks: List<CustomerFeedback>)

    @Query("DELETE FROM customer_feedbacks")
    suspend fun clearAllFeedbacks()
}
