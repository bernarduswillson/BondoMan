package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentStatisticsBinding
import com.example.transactionapp.ui.viewmodel.transaction.TransactionViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.Date

class Statistics : Fragment() {

    lateinit var binding: FragmentStatisticsBinding

    private val incomeValues = ArrayList<Entry>()
    private val expenseValues = ArrayList<Entry>()
    private val savingValues = ArrayList<Entry>()

    private val db: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater)

        dataListing()

        return binding.root
    }

    private fun dataListing() {
        db.sumOfIncome.observe(viewLifecycleOwner) {
            val sum = it.toString().reversed().chunked(3).joinToString(".").reversed()
            binding.incomeSum.text = sum
        }
        db.listOfIncome.observe(viewLifecycleOwner) {
            for (i in it.indices){
                incomeValues.add(Entry(i.toFloat(), it[i].toFloat()))
            }
            setChart("income")
        }

        db.sumOfExpense.observe(viewLifecycleOwner) {
            val sum = it.toString().reversed().chunked(3).joinToString(".").reversed()
            binding.expenseSum.text = sum
        }
        db.listOfExpense.observe(viewLifecycleOwner) {
            for (i in it.indices){
                expenseValues.add(Entry(i.toFloat(), it[i].toFloat()))
            }
            setChart("expense")
        }

        db.sumOfSaving.observe(viewLifecycleOwner) {
            val sum = it.toString().reversed().chunked(3).joinToString(".").reversed()
            binding.savingSum.text = sum
        }
        db.listOfSaving.observe(viewLifecycleOwner) {
            for (i in it.indices){
                savingValues.add(Entry(i.toFloat(), it[i].toFloat()))
            }
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


//    private fun setChart(type: String) {
//        val dataSet: LineDataSet
//
//        if (binding.incomeChart.data != null && binding.incomeChart.data.dataSetCount > 0) {
//            dataSet = binding.incomeChart.data.getDataSetByIndex(0) as LineDataSet
//            dataSet.values = incomeValues
//            binding.incomeChart.data.notifyDataChanged()
//            binding.incomeChart.notifyDataSetChanged()
//        } else {
//            dataSet = LineDataSet(incomeValues, "Income")
//            dataSet.setColors(ContextCompat.getColor(requireContext(), R.color.N1))
//            dataSet.setCircleColors(ContextCompat.getColor(requireContext(), R.color.N1))
//            dataSet.lineWidth = 2f
//            dataSet.circleRadius = 1f
//            dataSet.setDrawValues(false)
//
//            binding.incomeChart.apply {
//                description.isEnabled = false
//                legend.isEnabled = false
//                axisLeft.apply {
//                    setDrawLabels(false)
//                    setDrawGridLines(false)
//                    setDrawAxisLine(false)
//                }
//                axisRight.apply {
//                    setDrawLabels(false)
//                    setDrawGridLines(false)
//                    setDrawAxisLine(false)
//                }
//                xAxis.apply {
//                    setDrawLabels(false)
//                    setDrawGridLines(false)
//                    setDrawAxisLine(false)
//                }
//
//                setViewPortOffsets(0f, 0f, 0f, 0f)
//
//                animateXY(700, 1000, Easing.EaseInOutQuad)
//            }
//
//            val data = LineData(dataSet)
//            binding.incomeChart.data = data
//            binding.incomeChart.invalidate()
//        }
//    }
}