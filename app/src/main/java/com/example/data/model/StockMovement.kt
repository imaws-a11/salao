package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String, // "ENTRADA" ou "SAIDA"
    val quantity: Int,
    val reason: String, // "Compra Fornecedor", "Uso em Procedimento", "Venda Balcão", "Ajuste / Inventário", "Descarte / Vencido"
    val notes: String = "",
    val date: String, // YYYY-MM-DD HH:mm
    val timestamp: Long = System.currentTimeMillis()
)
