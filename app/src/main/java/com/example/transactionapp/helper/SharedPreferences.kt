package com.example.transactionapp.helper

import android.content.Context


fun getEmailSharedPref(context: Context): String {
    val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    return sharedPref.getString("email", "").toString()
}

fun changeEmailSharedPref(context: Context, email: String) {
    val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("email", email)
    editor.apply()
}

fun getTokenSharedPref(context: Context): String {
    val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    return sharedPref.getString("token", "").toString()
}

fun changeTokenSharedPref(context: Context, token: String) {
    val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("token", token)
    editor.apply()
}