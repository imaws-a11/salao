package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_loyalty")
data class ClientLoyalty(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val clientPhone: String,
    val totalPoints: Int = 0,
    val tier: String = "BRONZE", // BRONZE, PRATA, OURO, VIP_DIAMANTE
    val totalVisits: Int = 0,
    val lastVisitDate: String = "",
    val totalSpent: Double = 0.0
)
