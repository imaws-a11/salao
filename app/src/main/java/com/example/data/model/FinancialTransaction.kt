package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class FinancialTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val amount: Double,
    val type: String, // RECEITA, DESPESA
    val category: String, // SERVICO, PRODUTOS, ALUGUEL, COMISSAO, FORNECEDOR, CONTAS, OUTROS
    val date: String, // YYYY-MM-DD
    val paymentMethod: String = "PIX", // PIX, CARTAO_CREDITO, CARTAO_DEBITO, DINHEIRO
    val appointmentId: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
)
