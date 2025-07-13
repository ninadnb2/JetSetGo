package com.example.jetsetgo.data

import com.github.tehras.charts.bar.BarChartData

data class WeeklyStatsSummary(
    val totalSteps: Int,
    val averageSteps: Int,
    val mostActiveDay: Pair<String, Int>?,
    val leastActiveDay: Pair<String, Int>?,
    val daysGoalMet: Int,
    val badge: String,
    val barChartData: BarChartData
)

