package com.example.transactionapp.domain.db.repo

import com.example.transactionapp.domain.db.model.Transaction
import java.util.Date

interface TransactionDatabaseRepo {
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun getAllTransactions(): List<Transaction>
    suspend fun getAllTransactionsDesc(): List<Transaction>
    suspend fun getAllTransactionsAsc(): List<Transaction>
    suspend fun getAllFormattedDates(): List<Date>
    suspend fun getTransactionsByMonth(month: String): List<Transaction>
}