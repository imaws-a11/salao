package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class SalonService(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Cabelo, Unhas, Barba, Estética, Tratamentos
    val price: Double,
    val durationMinutes: Int = 45,
    val loyaltyPointsEarned: Int = 10,
    val description: String = "",
    val imageUrl: String = "",
    val iconType: String = "haircut"
)
