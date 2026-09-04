package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val clientPhone: String,
    val serviceId: Long,
    val serviceName: String,
    val servicePrice: Double,
    val professionalId: Long,
    val professionalName: String,
    val appointmentDate: String, // YYYY-MM-DD
    val appointmentTime: String, // HH:mm
    val status: String = "AGENDADO", // AGENDADO, CONFIRMADO, CONCLUIDO, CANCELADO
    val paymentStatus: String = "PENDENTE", // PENDENTE, PAGO_PIX, PAGO_CARTAO, PAGO_LOCAL
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
