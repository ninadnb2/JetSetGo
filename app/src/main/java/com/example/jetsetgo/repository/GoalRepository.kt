package com.example.jetsetgo.repository

import android.content.Context

class GoalRepository(context: Context) {
    private val prefs = context.getSharedPreferences("goal_prefs", Context.MODE_PRIVATE)
    private val key = "daily_goal"

    fun getGoal(): Int = prefs.getInt(key, 10000)

    fun saveGoal(goal: Int) {
        prefs.edit().putInt(key, goal).apply()
    }
}
