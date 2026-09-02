package com.example.data

import kotlinx.coroutines.flow.Flow

class BraiPayRepository(private val dao: BraiPayDao) {
    val allProducts: Flow<List<Product>> = dao.getAllProducts()
    val allTransactions: Flow<List<TransactionHistory>> = dao.getAllTransactions()
    val allNfcCards: Flow<List<GameNfcCard>> = dao.getAllNfcCards()
    val allJobs: Flow<List<JobItem>> = dao.getAllJobs()

    suspend fun insertProduct(product: Product) = dao.insertProduct(product)
    suspend fun deleteProductById(id: Int) = dao.deleteProductById(id)

    suspend fun insertTransaction(transaction: TransactionHistory) = dao.insertTransaction(transaction)
    suspend fun deleteTransactionById(id: Int) = dao.deleteTransactionById(id)
    suspend fun clearAllTransactions() = dao.clearAllTransactions()

    // Game NFC Cards Operations
    suspend fun insertNfcCard(card: GameNfcCard) = dao.insertNfcCard(card)
    suspend fun deleteNfcCardById(id: Int) = dao.deleteNfcCardById(id)
    suspend fun getNfcCardByUid(uid: String): GameNfcCard? = dao.getNfcCardByUid(uid)
    suspend fun getNfcCardByNumber(num: String): GameNfcCard? = dao.getNfcCardByNumber(num)

    // Jobs & Businesses Operations
    suspend fun insertJob(job: JobItem) = dao.insertJob(job)
    suspend fun deleteJobById(id: Int) = dao.deleteJobById(id)
    suspend fun deleteCompanyProducts() = dao.deleteCompanyProducts()
    suspend fun deleteCompanyJobs() = dao.deleteCompanyJobs()
    suspend fun clearAllJobs() = dao.clearAllJobs()
}
