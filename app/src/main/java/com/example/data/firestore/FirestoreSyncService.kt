package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.data.local.SalonDao
import com.example.data.model.Appointment
import com.example.data.model.ClientLoyalty
import com.example.data.model.FinancialTransaction
import com.example.data.model.Professional
import com.example.data.model.SalonService
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

enum class FirestoreSyncStatus {
    DISCONNECTED,
    CONNECTING,
    SYNCED,
    SYNCING,
    OFFLINE_LOCAL,
    ERROR
}

data class FirestoreSyncState(
    val status: FirestoreSyncStatus = FirestoreSyncStatus.OFFLINE_LOCAL,
    val isLiveConnected: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
    val syncedAppointments: Int = 0,
    val syncedTransactions: Int = 0,
    val statusMessage: String = "Modo Local Ativo (Room SQLite)",
    val salonId: String = "glowup_studio_central"
)

class FirestoreSyncService(private val context: Context) {

    private val tag = "FirestoreSyncService"
    private val prefs = context.getSharedPreferences("firestore_sync_prefs", Context.MODE_PRIVATE)

    private var firestore: FirebaseFirestore? = null
    private val listeners = mutableListOf<ListenerRegistration>()

    private val _syncState = MutableStateFlow(
        FirestoreSyncState(
            lastSyncTimestamp = prefs.getLong("last_sync_timestamp", 0L),
            salonId = prefs.getString("salon_id", "glowup_studio_central") ?: "glowup_studio_central"
        )
    )
    val syncState: StateFlow<FirestoreSyncState> = _syncState.asStateFlow()

    private var scope: CoroutineScope? = null
    private var salonDao: SalonDao? = null

    /**
     * Initializes Firestore connection, enables offline disk caching,
     * and sets up real-time snapshot listeners for appointments and financial transactions.
     */
    fun startSync(dao: SalonDao, coroutineScope: CoroutineScope) {
        this.salonDao = dao
        this.scope = coroutineScope

        val isAvailable = checkFirebaseAvailability()
        if (!isAvailable) {
            Log.i(tag, "FirebaseApp não configurado para nuvem online. Operando em modo Local-First (Room).")
            _syncState.value = _syncState.value.copy(
                status = FirestoreSyncStatus.OFFLINE_LOCAL,
                isLiveConnected = false,
                statusMessage = "Modo Local (Room Ativo). Adicione google-services.json para nuvem em tempo real."
            )
            return
        }

        try {
            _syncState.value = _syncState.value.copy(
                status = FirestoreSyncStatus.CONNECTING,
                statusMessage = "Conectando ao Firebase Firestore..."
            )

            val db = FirebaseFirestore.getInstance()
            try {
                // Enable modern persistent disk caching for offline-first reliability
                val settings = FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(
                        PersistentCacheSettings.newBuilder()
                            .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                            .build()
                    )
                    .build()
                db.firestoreSettings = settings
            } catch (e: Exception) {
                Log.w(tag, "Configuração de cache persistente: ${e.message}")
            }

            firestore = db
            _syncState.value = _syncState.value.copy(
                isLiveConnected = true,
                status = FirestoreSyncStatus.SYNCED,
                statusMessage = "Firestore Ativo • Sincronização em Tempo Real"
            )

            // Setup real-time snapshot listeners
            attachRealtimeListeners(dao, coroutineScope)

        } catch (e: Exception) {
            Log.e(tag, "Falha ao inicializar FirebaseFirestore: ${e.message}", e)
            _syncState.value = _syncState.value.copy(
                status = FirestoreSyncStatus.ERROR,
                isLiveConnected = false,
                statusMessage = "Erro ao conectar Firestore: ${e.localizedMessage}"
            )
        }
    }

    private fun checkFirebaseAvailability(): Boolean {
        return try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) return false
            val app = FirebaseApp.getInstance()
            val apiKey = app.options.apiKey
            val isPlaceholder = apiKey.isBlank() || apiKey.contains("Demo", ignoreCase = true) || apiKey.contains("12345")
            !isPlaceholder
        } catch (e: Exception) {
            false
        }
    }

    private fun attachRealtimeListeners(dao: SalonDao, coroutineScope: CoroutineScope) {
        val db = firestore ?: return
        val salonId = _syncState.value.salonId

        // 1. Real-time Appointments listener
        try {
            val appointmentReg = db.collection("salons")
                .document(salonId)
                .collection("appointments")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.w(tag, "Erro no listener de agendamentos: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        coroutineScope.launch(Dispatchers.IO) {
                            for (dc in snapshots.documentChanges) {
                                // Skip local changes to prevent duplicate round-trips
                                if (dc.document.metadata.hasPendingWrites()) continue

                                when (dc.type) {
                                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                                        val apt = dc.document.toAppointment()
                                        if (apt != null) {
                                            dao.insertAppointment(apt)
                                        }
                                    }
                                    DocumentChange.Type.REMOVED -> {
                                        val apt = dc.document.toAppointment()
                                        if (apt != null) {
                                            dao.deleteAppointment(apt)
                                        }
                                    }
                                }
                            }

                            _syncState.value = _syncState.value.copy(
                                syncedAppointments = snapshots.size(),
                                lastSyncTimestamp = System.currentTimeMillis()
                            )
                        }
                    }
                }
            listeners.add(appointmentReg)
        } catch (e: Exception) {
            Log.w(tag, "Falha ao registrar listener de agendamentos: ${e.message}")
        }

        // 2. Real-time Financial Transactions listener
        try {
            val transactionReg = db.collection("salons")
                .document(salonId)
                .collection("transactions")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.w(tag, "Erro no listener de transações: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        coroutineScope.launch(Dispatchers.IO) {
                            for (dc in snapshots.documentChanges) {
                                if (dc.document.metadata.hasPendingWrites()) continue

                                when (dc.type) {
                                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                                        val tx = dc.document.toFinancialTransaction()
                                        if (tx != null) {
                                            dao.insertTransaction(tx)
                                        }
                                    }
                                    DocumentChange.Type.REMOVED -> {
                                        val tx = dc.document.toFinancialTransaction()
                                        if (tx != null) {
                                            dao.deleteTransaction(tx)
                                        }
                                    }
                                }
                            }

                            _syncState.value = _syncState.value.copy(
                                syncedTransactions = snapshots.size(),
                                lastSyncTimestamp = System.currentTimeMillis()
                            )
                        }
                    }
                }
            listeners.add(transactionReg)
        } catch (e: Exception) {
            Log.w(tag, "Falha ao registrar listener de transações: ${e.message}")
        }
    }

    /**
     * Sincroniza um agendamento individual com o Firestore.
     */
    suspend fun syncAppointment(appointment: Appointment) {
        val db = firestore ?: return
        try {
            val docRef = db.collection("salons")
                .document(_syncState.value.salonId)
                .collection("appointments")
                .document(appointment.id.toString())

            docRef.set(appointment.toMap(), SetOptions.merge()).awaitTask()
            updateSyncTimestamp()
        } catch (e: Exception) {
            Log.w(tag, "syncAppointment falhou: ${e.message}")
        }
    }

    /**
     * Remove um agendamento do Firestore em tempo real.
     */
    suspend fun deleteAppointment(appointmentId: Long) {
        val db = firestore ?: return
        try {
            db.collection("salons")
                .document(_syncState.value.salonId)
                .collection("appointments")
                .document(appointmentId.toString())
                .delete()
                .awaitTask()
            updateSyncTimestamp()
        } catch (e: Exception) {
            Log.w(tag, "deleteAppointment falhou: ${e.message}")
        }
    }

    /**
     * Sincroniza uma transação financeira individual com o Firestore.
     */
    suspend fun syncTransaction(transaction: FinancialTransaction) {
        val db = firestore ?: return
        try {
            val docRef = db.collection("salons")
                .document(_syncState.value.salonId)
                .collection("transactions")
                .document(transaction.id.toString())

            docRef.set(transaction.toMap(), SetOptions.merge()).awaitTask()
            updateSyncTimestamp()
        } catch (e: Exception) {
            Log.w(tag, "syncTransaction falhou: ${e.message}")
        }
    }

    /**
     * Remove uma transação financeira do Firestore.
     */
    suspend fun deleteTransaction(transactionId: Long) {
        val db = firestore ?: return
        try {
            db.collection("salons")
                .document(_syncState.value.salonId)
                .collection("transactions")
                .document(transactionId.toString())
                .delete()
                .awaitTask()
            updateSyncTimestamp()
        } catch (e: Exception) {
            Log.w(tag, "deleteTransaction falhou: ${e.message}")
        }
    }

    /**
     * Realiza o envio em lote de todos os dados do salão (agendamentos, financeiro,
     * serviços, profissionais e fidelidade) para o Firestore.
     */
    suspend fun pushAllDataToCloud(dao: SalonDao): Result<String> {
        val db = firestore
        if (db == null) {
            // Em modo offline/desconectado, registra timestamp e retorna confirmação local
            val now = System.currentTimeMillis()
            updateSyncTimestamp(now)
            return Result.success("Dados locais seguros (Room SQLite). Conecte ao Firebase para espelhamento em nuvem.")
        }

        return try {
            _syncState.value = _syncState.value.copy(status = FirestoreSyncStatus.SYNCING)
            val salonId = _syncState.value.salonId

            val apts = dao.getAllAppointments().first()
            val txs = dao.getAllTransactions().first()
            val profs = dao.getAllProfessionals().first()
            val servs = dao.getAllServices().first()
            val clients = dao.getAllLoyaltyClients().first()

            val batch = db.batch()

            // 1. Appointments
            for (apt in apts) {
                val ref = db.collection("salons")
                    .document(salonId)
                    .collection("appointments")
                    .document(apt.id.toString())
                batch.set(ref, apt.toMap(), SetOptions.merge())
            }

            // 2. Transactions
            for (tx in txs) {
                val ref = db.collection("salons")
                    .document(salonId)
                    .collection("transactions")
                    .document(tx.id.toString())
                batch.set(ref, tx.toMap(), SetOptions.merge())
            }

            // 3. Professionals
            for (p in profs) {
                val ref = db.collection("salons")
                    .document(salonId)
                    .collection("professionals")
                    .document(p.id.toString())
                batch.set(ref, p.toMap(), SetOptions.merge())
            }

            // 4. Services
            for (s in servs) {
                val ref = db.collection("salons")
                    .document(salonId)
                    .collection("services")
                    .document(s.id.toString())
                batch.set(ref, s.toMap(), SetOptions.merge())
            }

            // 5. Clients
            for (c in clients) {
                val ref = db.collection("salons")
                    .document(salonId)
                    .collection("loyalty_clients")
                    .document(c.id.toString())
                batch.set(ref, c.toMap(), SetOptions.merge())
            }

            batch.commit().awaitTask()

            val now = System.currentTimeMillis()
            updateSyncTimestamp(now)
            _syncState.value = _syncState.value.copy(
                status = FirestoreSyncStatus.SYNCED,
                syncedAppointments = apts.size,
                syncedTransactions = txs.size,
                statusMessage = "Sincronizado com sucesso (${apts.size} agendamentos, ${txs.size} transações)"
            )
            Result.success("Sincronização concluída com o Firebase Firestore!")
        } catch (e: Exception) {
            _syncState.value = _syncState.value.copy(
                status = FirestoreSyncStatus.ERROR,
                statusMessage = "Erro na sincronização: ${e.message}"
            )
            Result.failure(e)
        }
    }

    /**
     * Baixa todos os documentos do Firestore e restaura no banco de dados Room.
     */
    suspend fun pullAllDataFromCloud(dao: SalonDao): Result<String> {
        val db = firestore
            ?: return Result.failure(Exception("Firestore não conectado. Conecte com google-services.json."))

        return try {
            _syncState.value = _syncState.value.copy(status = FirestoreSyncStatus.SYNCING)
            val salonId = _syncState.value.salonId

            val aptsSnap = db.collection("salons").document(salonId).collection("appointments").get().awaitTask()
            val txsSnap = db.collection("salons").document(salonId).collection("transactions").get().awaitTask()

            val apts = aptsSnap.documents.mapNotNull { it.toAppointment() }
            val txs = txsSnap.documents.mapNotNull { it.toFinancialTransaction() }

            if (apts.isNotEmpty()) {
                dao.insertAllAppointments(apts)
            }
            if (txs.isNotEmpty()) {
                dao.insertAllTransactions(txs)
            }

            val now = System.currentTimeMillis()
            updateSyncTimestamp(now)
            _syncState.value = _syncState.value.copy(
                status = FirestoreSyncStatus.SYNCED,
                syncedAppointments = apts.size,
                syncedTransactions = txs.size,
                statusMessage = "Restaurados da nuvem: ${apts.size} agendamentos e ${txs.size} transações"
            )
            Result.success("Dados restaurados do Firestore com sucesso!")
        } catch (e: Exception) {
            _syncState.value = _syncState.value.copy(
                status = FirestoreSyncStatus.ERROR,
                statusMessage = "Erro ao puxar dados: ${e.message}"
            )
            Result.failure(e)
        }
    }

    private fun updateSyncTimestamp(time: Long = System.currentTimeMillis()) {
        prefs.edit().putLong("last_sync_timestamp", time).apply()
        _syncState.value = _syncState.value.copy(lastSyncTimestamp = time)
    }

    fun stopSync() {
        for (listener in listeners) {
            listener.remove()
        }
        listeners.clear()
        firestore = null
    }

    // --- Mappers ---

    private fun Appointment.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "clientName" to clientName,
        "clientPhone" to clientPhone,
        "serviceId" to serviceId,
        "serviceName" to serviceName,
        "servicePrice" to servicePrice,
        "professionalId" to professionalId,
        "professionalName" to professionalName,
        "appointmentDate" to appointmentDate,
        "appointmentTime" to appointmentTime,
        "status" to status,
        "paymentStatus" to paymentStatus,
        "notes" to notes,
        "timestamp" to timestamp
    )

    private fun DocumentSnapshot.toAppointment(): Appointment? {
        return try {
            Appointment(
                id = getLong("id") ?: id.toLongOrNull() ?: 0L,
                clientName = getString("clientName") ?: "",
                clientPhone = getString("clientPhone") ?: "",
                serviceId = getLong("serviceId") ?: 1L,
                serviceName = getString("serviceName") ?: "",
                servicePrice = getDouble("servicePrice") ?: 0.0,
                professionalId = getLong("professionalId") ?: 1L,
                professionalName = getString("professionalName") ?: "",
                appointmentDate = getString("appointmentDate") ?: "",
                appointmentTime = getString("appointmentTime") ?: "",
                status = getString("status") ?: "AGENDADO",
                paymentStatus = getString("paymentStatus") ?: "PENDENTE",
                notes = getString("notes") ?: "",
                timestamp = getLong("timestamp") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun FinancialTransaction.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "description" to description,
        "amount" to amount,
        "type" to type,
        "category" to category,
        "date" to date,
        "paymentMethod" to paymentMethod,
        "appointmentId" to appointmentId,
        "timestamp" to timestamp
    )

    private fun DocumentSnapshot.toFinancialTransaction(): FinancialTransaction? {
        return try {
            FinancialTransaction(
                id = getLong("id") ?: id.toLongOrNull() ?: 0L,
                description = getString("description") ?: "",
                amount = getDouble("amount") ?: 0.0,
                type = getString("type") ?: "RECEITA",
                category = getString("category") ?: "SERVICO",
                date = getString("date") ?: "",
                paymentMethod = getString("paymentMethod") ?: "PIX",
                appointmentId = getLong("appointmentId"),
                timestamp = getLong("timestamp") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Professional.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "role" to role,
        "phone" to phone,
        "commissionPercent" to commissionPercent,
        "workingDays" to workingDays,
        "workingHours" to workingHours,
        "active" to active,
        "colorHex" to colorHex
    )

    private fun SalonService.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "category" to category,
        "price" to price,
        "durationMinutes" to durationMinutes,
        "loyaltyPointsEarned" to loyaltyPointsEarned,
        "description" to description,
        "imageUrl" to imageUrl,
        "iconType" to iconType
    )

    private fun ClientLoyalty.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "clientName" to clientName,
        "clientPhone" to clientPhone,
        "totalPoints" to totalPoints,
        "tier" to tier,
        "totalVisits" to totalVisits,
        "lastVisitDate" to lastVisitDate,
        "totalSpent" to totalSpent
    )
}

suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { result ->
        if (cont.isActive) cont.resume(result)
    }
    addOnFailureListener { exception ->
        if (cont.isActive) cont.resumeWithException(exception)
    }
    addOnCanceledListener {
        cont.cancel()
    }
}
