package com.example.transactionapp.domain.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val category: String,
    val nominal: Int,
    val location: String,
    val createdAt: String
)
