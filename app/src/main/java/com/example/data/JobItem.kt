package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "Jobs",
    val monthlyPayout: Double = 25000.0,
    val salePrice: Double = 100000.0,
    val owner: String = "MILAN JOVANOVIC",
    val businessCardNumber: String = "",
    val isPayoutEnabled: Boolean = true,
    val companyName: String = "",
    val workers: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
