package com.example.transactionapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.transactionapp.domain.db.model.Transaction
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class fileLoc(
    val fileName: String,
    val filePath: String
)

fun changeDateTypeToStandardDateLocal(type: Date): String {
    val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    return outputDateFormat.format(type)
}

fun changeNominalToIDN(nominal: Long): String{
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    return currencyFormat.format(nominal).split(",")[0]
}

fun saveExcelFile(transactions: List<Transaction>, type: String, context: Context): fileLoc {
    val workbook = when (type) {
        "xlsx" -> XSSFWorkbook()
        "xls" -> HSSFWorkbook()
        else -> XSSFWorkbook()
    }
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

    val fileName = "TransactionHistory_"+Date().time + "_."+type
    val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

    val file = File("$filePath/$fileName")
    val outputStream = FileOutputStream(file)
    workbook.write(outputStream)
    workbook.close()
    outputStream.close()

    return fileLoc(fileName, filePath)
}

fun sendExcelToEmail(transactions: List<Transaction>, context: Context, typeExcel: String, email: String){
    val fileStore = saveExcelFile(transactions, typeExcel, context)
    val file = File(fileStore.filePath,fileStore.fileName)
    val uri = FileProvider.getUriForFile(context, context.packageName+".provider", file)

    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, "Transaction History")
        putExtra(
            Intent.EXTRA_TEXT,
            "Ini adalah history transaksi anda selama menggunakan Bondoman\n\nTerimakasih sudah menggunakan aplikasi kami"
        )
        putExtra(
            Intent.EXTRA_STREAM,
            uri
        )
    }


    (context as Activity).startActivityForResult(Intent.createChooser(emailIntent, "send email"), 1002)
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}