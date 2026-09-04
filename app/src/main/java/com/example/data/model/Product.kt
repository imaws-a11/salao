package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Capilar, Coloração, Esmaltes, Pele & Estética, Barba, Descartáveis
    val sku: String = "",
    val currentStock: Int,
    val minStockAlert: Int, // Reorder point (Ponto de Reposição)
    val costPrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val unit: String = "un", // un, ml, g, kit, cx
    val supplier: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val isLowStock: Boolean
        get() = currentStock <= minStockAlert
}
