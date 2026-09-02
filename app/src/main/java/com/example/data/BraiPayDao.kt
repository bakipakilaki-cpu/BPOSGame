package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BraiPayDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionHistory)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    // Game NFC Cards Operations
    @Query("SELECT * FROM nfc_cards ORDER BY cardHolder ASC")
    fun getAllNfcCards(): Flow<List<GameNfcCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNfcCard(card: GameNfcCard)

    @Query("DELETE FROM nfc_cards WHERE id = :id")
    suspend fun deleteNfcCardById(id: Int)

    @Query("SELECT * FROM nfc_cards WHERE cardUid = :uid LIMIT 1")
    suspend fun getNfcCardByUid(uid: String): GameNfcCard?

    @Query("SELECT * FROM nfc_cards WHERE cardNumber = :num LIMIT 1")
    suspend fun getNfcCardByNumber(num: String): GameNfcCard?

    // Jobs & Businesses Operations
    @Query("SELECT * FROM jobs ORDER BY id DESC")
    fun getAllJobs(): Flow<List<JobItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobItem)

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteJobById(id: Int)

    @Query("DELETE FROM products WHERE category = 'Company' OR category LIKE '%Company%'")
    suspend fun deleteCompanyProducts()

    @Query("DELETE FROM jobs WHERE category LIKE '%Company%' OR companyName != '' OR title LIKE '%Studio%' OR title LIKE '%Labs%' OR title LIKE '%Company%' OR category LIKE '%Job%'")
    suspend fun deleteCompanyJobs()

    @Query("DELETE FROM jobs")
    suspend fun clearAllJobs()
}
