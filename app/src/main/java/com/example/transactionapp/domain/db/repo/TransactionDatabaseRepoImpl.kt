package com.example.transactionapp.domain.db.repo

import com.example.transactionapp.domain.db.dao.TransactionDao
import com.example.transactionapp.domain.db.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class TransactionDatabaseRepoImpl @Inject constructor(
    private val transactionDao: TransactionDao
): TransactionDatabaseRepo {
    override suspend fun insertTransaction(transaction: Transaction) = withContext(Dispatchers.Default){
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: Transaction) = withContext(Dispatchers.Default){
        transactionDao.deleteTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: Transaction) = withContext(Dispatchers.Default){
        transactionDao.updateTransaction(transaction)
    }

    override suspend fun getAllTransactions(): List<Transaction> = withContext(Dispatchers.Default){
        transactionDao.getAllTransactions()
    }

    override suspend fun getAllTransactionsDesc(): List<Transaction> = withContext(Dispatchers.Default){
        transactionDao.getAllTransactionsDesc()
    }

    override suspend fun getAllTransactionsAsc(): List<Transaction> = withContext(Dispatchers.Default){
        transactionDao.getAllTransactionsAsc()
    }

    override suspend fun getAllFormattedDates(): List<Date> = withContext(Dispatchers.Default){
        transactionDao.getAllFormattedDates()
    }

    override suspend fun getTransactionsByMonth(month: String): List<Transaction> = withContext(Dispatchers.Default){
        transactionDao.getTransactionsByMonth(month)
    }

    override suspend fun getTransactionById(id: Int): Transaction = withContext(Dispatchers.Default){
        transactionDao.getTransactionById(id)
    }

}