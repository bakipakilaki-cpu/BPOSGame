package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val amount: Double,
    val status: String, // PENDING, COMPLETED, FAILED, CANCELLED
    val paymentMethod: String, // RaiPOS, Sandbox, External Intent
    val transactionId: String,
    val currency: String = "EUR", // EUR or RSD
    val timestamp: Long = System.currentTimeMillis()
)
