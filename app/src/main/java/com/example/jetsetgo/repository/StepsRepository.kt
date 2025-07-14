package com.example.jetsetgo.repository

import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StepRepository(context: Context) {
    private val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
    private val baseKey = "base_step_count"

    val today: String = LocalDate.now().toString()

    fun saveStepsForDate(date: String, steps: Int) {
        prefs.edit().putInt(date, steps).apply()
    }

    fun getStepsForDate(date: String): Int {
        return prefs.getInt(date, 0)
    }

    fun getWeeklyStepData(): List<Pair<String, Int>> {
        val formatter = DateTimeFormatter.ofPattern("EEE")
        return (0..6).map { i ->
            val date = LocalDate.now().minusDays(i.toLong())
            val steps = getStepsForDate(date.toString())
            formatter.format(date) to steps
        }.reversed()
    }

    fun getBaseStepForDate(date: String): Int {
        return prefs.getInt("${baseKey}_$date", -1)
    }

    fun saveBaseStepForDate(date: String, baseStep: Int) {
        prefs.edit().putInt("${baseKey}_$date", baseStep).apply()
    }

}
