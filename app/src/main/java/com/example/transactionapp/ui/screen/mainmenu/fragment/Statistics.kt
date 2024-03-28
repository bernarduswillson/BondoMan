package com.example.transactionapp.ui.screen.mainmenu.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.transactionapp.R
import com.example.transactionapp.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class Statistics : Fragment() {

    lateinit var binding: FragmentStatisticsBinding

    val profitValues = ArrayList<Entry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater)

        dataListing()

        return binding.root
    }

    fun dataListing() {
        profitValues.add(Entry(0f, 134f))
        profitValues.add(Entry(1f, 53f))
        profitValues.add(Entry(2f, 142f))
        profitValues.add(Entry(3f, 123f))
        profitValues.add(Entry(4f, 124f))
        profitValues.add(Entry(5f, 200f))
        profitValues.add(Entry(6f, 134f))
        setChart()
    }

    fun setChart() {
        val dataSet: LineDataSet

        if (binding.lineChart.data != null && binding.lineChart.data.dataSetCount > 0) {
            dataSet = binding.lineChart.data.getDataSetByIndex(0) as LineDataSet
            dataSet.values = profitValues
            binding.lineChart.data.notifyDataChanged()
            binding.lineChart.notifyDataSetChanged()
        } else {
            dataSet = LineDataSet(profitValues, "Profit")
            dataSet.setColors(ContextCompat.getColor(requireContext(), R.color.N1))
            dataSet.setCircleColors(ContextCompat.getColor(requireContext(), R.color.N1))
            dataSet.lineWidth = 2f
            dataSet.circleRadius = 3f
            dataSet.setDrawValues(false)

            binding.lineChart.apply {
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
            binding.lineChart.data = data
            binding.lineChart.invalidate()
        }
    }
}