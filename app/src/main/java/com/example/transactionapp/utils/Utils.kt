package com.example.transactionapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun changeDateTypeToStandardDateLocal(type: Date): String {
    val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

    return outputDateFormat.format(type)
}