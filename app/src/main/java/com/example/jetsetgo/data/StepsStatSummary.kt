package com.example.jetsetgo.data

import androidx.compose.ui.graphics.Color

data class StepStatsSummary(
    val stepCount: Int,
    val goal: Int,
    val progress: Float,
    val rawProgressPercent: Int,
    val distanceInKm: Double,
    val caloriesBurned: Double,
    val motivationText: String,
    val progressColor: Color
)
