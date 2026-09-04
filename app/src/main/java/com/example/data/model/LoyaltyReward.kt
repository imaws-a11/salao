package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loyalty_rewards")
data class LoyaltyReward(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val pointsRequired: Int,
    val description: String,
    val discountValue: Double = 0.0,
    val iconName: String = "gift"
)
