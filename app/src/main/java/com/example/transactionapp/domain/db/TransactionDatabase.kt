package com.example.transactionapp.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.transactionapp.domain.db.dao.TransactionDao
import com.example.transactionapp.domain.db.model.Transaction

@Database(entities = [Transaction::class], version = 1)
abstract class TransactionDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}