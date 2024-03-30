package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentStatisticsBinding
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.Arrays
import java.util.Calendar
import java.util.Date

class Statistics : Fragment() {

    lateinit var binding: FragmentStatisticsBinding

    private val incomeValues = ArrayList<Entry>()
    private val expenseValues = ArrayList<Entry>()
    private val savingValues = ArrayList<Entry>()

    var incomeFetched = false
    var expenseFetched = false
    var savingFetched = false


    private val db: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater)

        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val arrayAdp = ArrayAdapter(requireActivity(), R.layout.selected_dropdown_item, months)
        binding.monthInput.adapter = arrayAdp

        val calendar = Calendar.getInstance()
        val currentMonthIndex = calendar.get(Calendar.MONTH)
        binding.monthInput.setSelection(currentMonthIndex)

        dataListing()

        binding.monthInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = position + 1
                val selectedYear = binding.yearInput.selectedItem.toString().toInt()
                db.getStatisticByMonth(getDateForMonth(selectedMonth, selectedYear))

                renderCharts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.yearInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedYear = binding.yearInput.selectedItem.toString().toInt()
                val selectedMonth = binding.monthInput.selectedItemPosition + 1
                db.getStatisticByMonth(getDateForMonth(selectedMonth, selectedYear))

                renderCharts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        return binding.root
    }

    private fun getDateForMonth(month: Int, year: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        return calendar.time
    }


    private fun dataListing() {
        db.listOfIncome.observe(viewLifecycleOwner) {
            incomeValues.clear()
            for (i in it.indices) {
                incomeValues.add(Entry(i.toFloat(), it[i].toFloat()))
            }
            incomeFetched = true
            renderCharts()
        }

        db.listOfExpense.observe(viewLifecycleOwner) {
            expenseValues.clear()
            for (i in it.indices) {
                expenseValues.add(Entry(i.toFloat(), it[i].toFloat()))
            }
            expenseFetched = true
            renderCharts()
        }

        db.listOfSaving.observe(viewLifecycleOwner) {
            savingValues.clear()
            for (i in it.indices) {
                savingValues.add(Entry(i.toFloat(), it[i].toFloat()))
            }
            savingFetched = true
            renderCharts()
        }


        db.sumOfIncome.observe(viewLifecycleOwner) {
            val sum = it.toString().reversed().chunked(3).joinToString(".").reversed()
            binding.incomeSum.text = sum
        }

        db.sumOfExpense.observe(viewLifecycleOwner) {
            val sum = it.toString().reversed().chunked(3).joinToString(".").reversed()
            binding.expenseSum.text = sum
        }

        db.sumOfSaving.observe(viewLifecycleOwner) {
            val sum = it.toString().reversed().chunked(3).joinToString(".").reversed()
            binding.savingSum.text = sum
        }

        db.dateAll.observe(viewLifecycleOwner) { transactionDateList ->
            val yearsList = transactionDateList
                .map { transactionDate ->
                    transactionDate.date.split(" ")[2]
                }
                .distinct()

            val years = yearsList.toTypedArray()
            Log.d("Years", "onCreateView: ${Arrays.toString(years)}")

            val arrayAdpYear = ArrayAdapter(requireActivity(), R.layout.selected_dropdown_item, years)
            binding.yearInput.adapter = arrayAdpYear

            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR).toString()
            val currentYearIndex = years.indexOf(currentYear)
            binding.yearInput.setSelection(currentYearIndex)
        }
    }

    private fun renderCharts() {
        if (incomeFetched && expenseFetched && savingFetched) {
            setChart("income")
            setChart("expense")
            setChart("saving")
        }
    }

    private fun setChart(type: String) {
        val dataSet: LineDataSet

        val chart = when (type) {
            "income" -> binding.incomeChart
            "expense" -> binding.expenseChart
            "saving" -> binding.savingChart
            else -> binding.incomeChart
        }

        if (chart.data != null && chart.data.dataSetCount > 0) {
            dataSet = chart.data.getDataSetByIndex(0) as LineDataSet
            dataSet.values = getDataValues(type)
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            dataSet = LineDataSet(getDataValues(type), type.capitalize())
            dataSet.setColors(ContextCompat.getColor(requireContext(), R.color.N1))
            dataSet.lineWidth = 2f
            dataSet.setDrawCircles(false)
            dataSet.setDrawValues(false)

            chart.apply {
                description.isEnabled = false
                legend.isEnabled = false
                axisLeft.apply {
                    setDrawLabels(false)
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                }
                axisRight.apply {
                    setDrawLabels(false)
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                }
                xAxis.apply {
                    setDrawLabels(false)
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                }

                setViewPortOffsets(0f, 0f, 0f, 0f)

                animateXY(700, 1000, Easing.EaseInOutQuad)
            }

            val data = LineData(dataSet)
            chart.data = data
            chart.invalidate()
        }
    }

    private fun getDataValues(type: String): List<Entry> {
        return when (type) {
            "income" -> incomeValues
            "expense" -> expenseValues
            "saving" -> savingValues
            else -> emptyList()
        }
    }
}