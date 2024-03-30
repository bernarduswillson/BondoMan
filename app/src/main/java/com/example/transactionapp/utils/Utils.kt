package com.example.transactionapp.utils

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.transactionapp.domain.db.model.Transaction
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
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

    return currencyFormat.format(nominal).split(",")[0]
}

fun createExcelFile(transactions: List<Transaction>, type: String, context: Context) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Transaction History")

    val headerRow = sheet.createRow(0)
    val headerVal = listOf(
        "Title",
        "Category",
        "Nominal",
        "Location",
        "Created At"
    )

    headerVal.forEachIndexed { index, value ->
        headerRow.createCell(index).setCellValue(value)
    }

    transactions.forEachIndexed { index, transaction ->
        val row = sheet.createRow(index + 1)
        val cell = listOf(
            transaction.title,
            transaction.category,
            transaction.nominal.toString(),
            transaction.location,
            changeDateTypeToStandardDateLocal(transaction.createdAt)
        )

        cell.forEachIndexed { cellIndex, cellValue ->
            row.createCell(cellIndex).setCellValue(cellValue)
        }
    }

    val fileName = "TransactionHistory_"+Date().toString() + "_."+type

    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/$fileName")
    Log.d("File", context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())
    val outputStream = FileOutputStream(file)
    workbook.write(outputStream)
    workbook.close()
    outputStream.close()
}