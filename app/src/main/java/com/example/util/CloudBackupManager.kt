package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.model.Appointment
import com.example.data.model.ClientLoyalty
import com.example.data.model.FinancialTransaction
import com.example.data.model.LoyaltyReward
import com.example.data.model.Professional
import com.example.data.model.SalonService
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CloudBackupBundle(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val salonName: String = "Salão Gestão",
    val appointments: List<Appointment>,
    val transactions: List<FinancialTransaction>,
    val professionals: List<Professional>,
    val services: List<SalonService>,
    val loyaltyClients: List<ClientLoyalty>,
    val rewards: List<LoyaltyReward>
)

object CloudBackupManager {

    private const val PREFS_NAME = "salon_cloud_backup_prefs"
    private const val KEY_LAST_BACKUP = "last_backup_time"
    private const val KEY_AUTO_SYNC = "auto_sync_enabled"
    private const val KEY_CLOUD_STORAGE_ID = "cloud_vault_id"
    private const val KEY_CACHED_CLOUD_JSON = "cached_cloud_json"

    fun getLastBackupTime(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_BACKUP, System.currentTimeMillis() - (1000L * 60 * 60 * 3))
    }

    fun setLastBackupTime(context: Context, time: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_BACKUP, time).apply()
    }

    fun isAutoSyncEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_AUTO_SYNC, true)
    }

    fun setAutoSyncEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AUTO_SYNC, enabled).apply()
    }

    fun getCloudVaultId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var id = prefs.getString(KEY_CLOUD_STORAGE_ID, null)
        if (id == null) {
            id = "VAULT-BR-${(1000..9999).random()}-${(1000..9999).random()}"
            prefs.edit().putString(KEY_CLOUD_STORAGE_ID, id).apply()
        }
        return id
    }

    fun saveCloudSnapshot(context: Context, json: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_CACHED_CLOUD_JSON, json)
            .putLong(KEY_LAST_BACKUP, System.currentTimeMillis())
            .apply()
    }

    fun getCloudSnapshot(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CACHED_CLOUD_JSON, null)
    }

    /**
     * Serializes all domain models to a clean, formatted JSON structure.
     */
    fun serializeBackup(bundle: CloudBackupBundle): String {
        val root = JSONObject()
        root.put("version", bundle.version)
        root.put("timestamp", bundle.timestamp)
        root.put("salonName", bundle.salonName)

        // Appointments
        val aptArray = JSONArray()
        for (a in bundle.appointments) {
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("clientName", a.clientName)
            obj.put("clientPhone", a.clientPhone)
            obj.put("serviceId", a.serviceId)
            obj.put("serviceName", a.serviceName)
            obj.put("servicePrice", a.servicePrice)
            obj.put("professionalId", a.professionalId)
            obj.put("professionalName", a.professionalName)
            obj.put("appointmentDate", a.appointmentDate)
            obj.put("appointmentTime", a.appointmentTime)
            obj.put("status", a.status)
            obj.put("paymentStatus", a.paymentStatus)
            obj.put("notes", a.notes)
            obj.put("timestamp", a.timestamp)
            aptArray.put(obj)
        }
        root.put("appointments", aptArray)

        // Transactions
        val txArray = JSONArray()
        for (t in bundle.transactions) {
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("description", t.description)
            obj.put("amount", t.amount)
            obj.put("type", t.type)
            obj.put("category", t.category)
            obj.put("date", t.date)
            obj.put("paymentMethod", t.paymentMethod)
            obj.put("appointmentId", t.appointmentId ?: -1)
            obj.put("timestamp", t.timestamp)
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        // Professionals
        val profArray = JSONArray()
        for (p in bundle.professionals) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("role", p.role)
            obj.put("phone", p.phone)
            obj.put("commissionPercent", p.commissionPercent)
            obj.put("workingDays", p.workingDays)
            obj.put("workingHours", p.workingHours)
            obj.put("active", p.active)
            obj.put("colorHex", p.colorHex)
            profArray.put(obj)
        }
        root.put("professionals", profArray)

        // Services
        val servArray = JSONArray()
        for (s in bundle.services) {
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("name", s.name)
            obj.put("category", s.category)
            obj.put("price", s.price)
            obj.put("durationMinutes", s.durationMinutes)
            obj.put("loyaltyPointsEarned", s.loyaltyPointsEarned)
            servArray.put(obj)
        }
        root.put("services", servArray)

        // Loyalty
        val loyArray = JSONArray()
        for (l in bundle.loyaltyClients) {
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("clientName", l.clientName)
            obj.put("clientPhone", l.clientPhone)
            obj.put("totalPoints", l.totalPoints)
            obj.put("tier", l.tier)
            obj.put("totalVisits", l.totalVisits)
            obj.put("lastVisitDate", l.lastVisitDate)
            obj.put("totalSpent", l.totalSpent)
            loyArray.put(obj)
        }
        root.put("loyaltyClients", loyArray)

        // Rewards
        val rewArray = JSONArray()
        for (r in bundle.rewards) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("title", r.title)
            obj.put("pointsRequired", r.pointsRequired)
            obj.put("description", r.description)
            obj.put("discountValue", r.discountValue)
            obj.put("iconName", r.iconName)
            rewArray.put(obj)
        }
        root.put("rewards", rewArray)

        return root.toString(2)
    }

    /**
     * Parses a backup JSON string back into a CloudBackupBundle.
     */
    fun deserializeBackup(jsonStr: String): CloudBackupBundle {
        val root = JSONObject(jsonStr)
        val version = root.optInt("version", 1)
        val timestamp = root.optLong("timestamp", System.currentTimeMillis())
        val salonName = root.optString("salonName", "Salão Gestão")

        val appointments = mutableListOf<Appointment>()
        val aptArray = root.optJSONArray("appointments") ?: JSONArray()
        for (i in 0 until aptArray.length()) {
            val obj = aptArray.getJSONObject(i)
            appointments.add(
                Appointment(
                    id = obj.optLong("id", 0),
                    clientName = obj.optString("clientName", ""),
                    clientPhone = obj.optString("clientPhone", ""),
                    serviceId = obj.optLong("serviceId", 0),
                    serviceName = obj.optString("serviceName", ""),
                    servicePrice = obj.optDouble("servicePrice", 0.0),
                    professionalId = obj.optLong("professionalId", 0),
                    professionalName = obj.optString("professionalName", ""),
                    appointmentDate = obj.optString("appointmentDate", ""),
                    appointmentTime = obj.optString("appointmentTime", ""),
                    status = obj.optString("status", "AGENDADO"),
                    paymentStatus = obj.optString("paymentStatus", "PENDENTE"),
                    notes = obj.optString("notes", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            )
        }

        val transactions = mutableListOf<FinancialTransaction>()
        val txArray = root.optJSONArray("transactions") ?: JSONArray()
        for (i in 0 until txArray.length()) {
            val obj = txArray.getJSONObject(i)
            val aptId = obj.optLong("appointmentId", -1)
            transactions.add(
                FinancialTransaction(
                    id = obj.optLong("id", 0),
                    description = obj.optString("description", ""),
                    amount = obj.optDouble("amount", 0.0),
                    type = obj.optString("type", "RECEITA"),
                    category = obj.optString("category", "OUTROS"),
                    date = obj.optString("date", ""),
                    paymentMethod = obj.optString("paymentMethod", "PIX"),
                    appointmentId = if (aptId > 0) aptId else null,
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            )
        }

        val professionals = mutableListOf<Professional>()
        val profArray = root.optJSONArray("professionals") ?: JSONArray()
        for (i in 0 until profArray.length()) {
            val obj = profArray.getJSONObject(i)
            professionals.add(
                Professional(
                    id = obj.optLong("id", 0),
                    name = obj.optString("name", ""),
                    role = obj.optString("role", ""),
                    phone = obj.optString("phone", ""),
                    commissionPercent = obj.optInt("commissionPercent", 50),
                    workingDays = obj.optString("workingDays", "Terça a Sábado"),
                    workingHours = obj.optString("workingHours", "09:00 - 19:00"),
                    active = obj.optBoolean("active", true),
                    colorHex = obj.optLong("colorHex", 0xFF9E475A)
                )
            )
        }

        val services = mutableListOf<SalonService>()
        val servArray = root.optJSONArray("services") ?: JSONArray()
        for (i in 0 until servArray.length()) {
            val obj = servArray.getJSONObject(i)
            services.add(
                SalonService(
                    id = obj.optLong("id", 0),
                    name = obj.optString("name", ""),
                    category = obj.optString("category", "Cabelo"),
                    price = obj.optDouble("price", 0.0),
                    durationMinutes = obj.optInt("durationMinutes", 45),
                    loyaltyPointsEarned = obj.optInt("loyaltyPointsEarned", 10)
                )
            )
        }

        val loyaltyClients = mutableListOf<ClientLoyalty>()
        val loyArray = root.optJSONArray("loyaltyClients") ?: JSONArray()
        for (i in 0 until loyArray.length()) {
            val obj = loyArray.getJSONObject(i)
            loyaltyClients.add(
                ClientLoyalty(
                    id = obj.optLong("id", 0),
                    clientName = obj.optString("clientName", ""),
                    clientPhone = obj.optString("clientPhone", ""),
                    totalPoints = obj.optInt("totalPoints", 0),
                    tier = obj.optString("tier", "BRONZE"),
                    totalVisits = obj.optInt("totalVisits", 0),
                    lastVisitDate = obj.optString("lastVisitDate", ""),
                    totalSpent = obj.optDouble("totalSpent", 0.0)
                )
            )
        }

        val rewards = mutableListOf<LoyaltyReward>()
        val rewArray = root.optJSONArray("rewards") ?: JSONArray()
        for (i in 0 until rewArray.length()) {
            val obj = rewArray.getJSONObject(i)
            rewards.add(
                LoyaltyReward(
                    id = obj.optLong("id", 0),
                    title = obj.optString("title", ""),
                    pointsRequired = obj.optInt("pointsRequired", 50),
                    description = obj.optString("description", ""),
                    discountValue = obj.optDouble("discountValue", 0.0),
                    iconName = obj.optString("iconName", "gift")
                )
            )
        }

        return CloudBackupBundle(
            version = version,
            timestamp = timestamp,
            salonName = salonName,
            appointments = appointments,
            transactions = transactions,
            professionals = professionals,
            services = services,
            loyaltyClients = loyaltyClients,
            rewards = rewards
        )
    }

    fun shareBackupFile(context: Context, json: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_SUBJECT, "Backup_SalaoGestao_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.json")
            putExtra(Intent.EXTRA_TEXT, json)
        }
        val chooser = Intent.createChooser(intent, "Exportar Backup Seguro do Salão")
        context.startActivity(chooser)
    }
}
