package com.example.jetsetgo.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StepRepository(context: Context) {
    private val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
    private val baseKey = "base_step_count"
    private val firebaseDb = FirebaseDatabase.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    val today: String = LocalDate.now().toString()

    fun saveStepsForDate(date: String, steps: Int) {
        prefs.edit().putInt(date, steps).apply()
        firebaseDb.child("users")
            .child(userId)
            .child("steps")
            .child(date)
            .setValue(steps)
            .addOnSuccessListener {
                Log.d("FIREBASE_WRITE", "Saved $steps for $date")
            }
            .addOnFailureListener {
                Log.e("FIREBASE_WRITE", "Failed to save: ${it.message}")
            }
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

        firebaseDb.child("users")
            .child(userId)
            .child("baseSteps")
            .child(date)
            .setValue(baseStep)
    }
}
