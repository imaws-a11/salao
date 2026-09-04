package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "professionals")
data class Professional(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val role: String, // Cabeleireiro(a), Colorista, Manicure, Barbeiro, Designer de Sobrancelhas, Esteticista
    val phone: String,
    val commissionPercent: Int = 50, // % de comissão
    val workingDays: String = "Terça a Sábado",
    val workingHours: String = "09:00 às 19:00",
    val active: Boolean = true,
    val colorHex: Long = 0xFF9E475A
)
