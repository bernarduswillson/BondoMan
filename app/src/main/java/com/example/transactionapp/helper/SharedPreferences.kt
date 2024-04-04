package com.example.transactionapp.helper

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.transactionapp.BuildConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

val cryptoManager = CryptoManager()
fun getEmailSharedPref(context: Context): String {
    return try {
        val file = File( "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/${BuildConfig.EMAIL_FILE}.txt")

        val decrypt = cryptoManager.decrypt(file.inputStream())
        decrypt.decodeToString().substring(16)
    } catch (e: Exception) {
        Log.e("getEmailSharedPref", e.message.toString())
        ""
    }
}

fun changeEmailSharedPref(context: Context, email: String) {
    val emailInput = "1234567890123456$email".encodeToByteArray()
    val file = File( "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/${BuildConfig.EMAIL_FILE}.txt")
    val fos = FileOutputStream(file)
    cryptoManager.encrypt(emailInput, fos)
}

fun getTokenSharedPref(context: Context): String {
    return try {
        val file = File( "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/${BuildConfig.TOKEN_FILE}.txt")
        val decrypt = cryptoManager.decrypt(FileInputStream(file))
        "ey${decrypt.decodeToString().substringAfter("ey")}"
    } catch (e: Exception) {
        Log.e("getTokenSharedPref", e.message.toString())
        ""
    }
}

fun changeTokenSharedPref(context: Context, token: String) {
    val tokenInput = "1234567890123456$token".encodeToByteArray()
    val file = File( "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/${BuildConfig.TOKEN_FILE}.txt")
    val fos = FileOutputStream(file)
    cryptoManager.encrypt(tokenInput, fos)
}