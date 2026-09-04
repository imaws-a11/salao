package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_feedbacks")
data class CustomerFeedback(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appointmentId: Long = 0,
    val clientName: String,
    val clientPhone: String = "",
    val professionalId: Long,
    val professionalName: String,
    val serviceId: Long,
    val serviceName: String,
    val rating: Int, // 1 to 5 stars
    val comment: String = "",
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)
