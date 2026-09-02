package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nfc_cards")
data class GameNfcCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardHolder: String,
    val cardNumber: String,
    val cardUid: String = "", // NFC UID (uppercase hex)
    val balance: Double,
    val currency: String = "EUR",
    val colorHex: String = "#6200EE", // Custom background color for card
    val isKidCard: Boolean = false,
    val parentCardId: Int? = null,
    val dailySpendingLimit: Double = 0.0,
    val todaySpent: Double = 0.0,
    val bankPassword: String = "",
    val customImageUri: String? = null,
    val expiryDate: String = "08/28",
    val cvv: String = "342",
    val creditDebt: Double = 0.0,
    val creditLimit: Double = 0.0,
    val creditInterestRate: Double = 0.10, // 10% Kamata
    val monthlyRepaymentPct: Double = 0.20, // 20% Monthly repayment
    val sharedWithUser: String = "",
    val bankProvider: String = "",
    val cardStyle: String = "Gold Edition",
    val createdAt: Long = System.currentTimeMillis()
)
