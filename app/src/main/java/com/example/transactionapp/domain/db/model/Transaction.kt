package com.example.transactionapp.domain.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val category: String,
    val nominal: Long,
    val location: String,
    val lat: Double,
    val long: Double,
    val createdAt: Date
)
