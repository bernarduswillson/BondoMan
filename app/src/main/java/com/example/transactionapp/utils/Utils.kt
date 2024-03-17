package com.example.transactionapp.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun changeDateTypeToStandardDateLocal(type: Date): String {
    val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    return outputDateFormat.format(type)
}

fun changeNominalToIDN(nominal: Long): String{
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    return currencyFormat.format(nominal)
}