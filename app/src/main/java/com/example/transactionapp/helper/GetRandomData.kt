package com.example.transactionapp.helper

class GetRandomData {
    fun getRandomTitle(): String {
        val title = arrayOf(
            "Baju",
            "Makanan",
            "Minuman",
            "Bensin",
            "Pulsa",
            "Makanan"
        )

        return title.random()
    }

    fun getRandomNominal(): Long {
        val nominal = arrayOf(
            10000,
            20000,
            30000,
            40000,
            50000,
            60000,
            70000,
            80000,
            90000,
            100000
        )

        return nominal.random().toLong()
    }

    fun getRandomCategory(): String {
        val category = arrayOf(
            "Income",
            "Expense",
            "Savings"
        )

        return category.random()
    }
}