package com.example.transactionapp.ui.viewmodel.transaction

import androidx.lifecycle.ViewModel
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.domain.db.repo.TransactionDatabaseRepoImpl
import javax.inject.Inject

class Transaction @Inject constructor(
    private val transactionDatabaseRepoImpl: TransactionDatabaseRepoImpl
): ViewModel() {
    private val _transaction: List<Transaction> = emptyList()

    val transaction: List<Transaction>
        get() = _transaction

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDatabaseRepoImpl.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDatabaseRepoImpl.deleteTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDatabaseRepoImpl.updateTransaction(transaction)
    }

    suspend fun getTransactions(type: String) {
       when (type) {
            "desc" -> transactionDatabaseRepoImpl.getAllTransactionsDesc()
            "asc" -> transactionDatabaseRepoImpl.getAllTransactionsAsc()
            else -> transactionDatabaseRepoImpl.getAllTransactions()
       }
    }
}