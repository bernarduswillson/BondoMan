package com.example.transactionapp.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.transactionapp.domain.db.dao.TransactionDao
import com.example.transactionapp.domain.db.model.Transaction
import com.example.transactionapp.helper.Converters

@Database(entities = [Transaction::class], version = 1)
@TypeConverters(Converters::class)
abstract class TransactionDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}