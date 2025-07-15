package com.example.jetsetgo.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StepRepository(context: Context) {
    private val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
    private val baseKey = "base_step_count"
    private val firebaseDb = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userId: String
        get() = auth.currentUser?.uid ?: "guest"

    val today: String = LocalDate.now().toString()

    private fun stepKey(date: String) = "${userId}_$date"
    private fun baseStepKey(date: String) = "${baseKey}_${userId}_$date"

    fun saveStepsForDate(date: String, steps: Int) {
        prefs.edit().putInt(stepKey(date), steps).apply()
        firebaseDb.child("users")
            .child(userId)
            .child("steps")
            .child(date)
            .setValue(steps)
    }

    fun getStepsForDate(date: String): Int {
        return prefs.getInt(stepKey(date), 0)
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
        return prefs.getInt(baseStepKey(date), -1)
    }

    fun saveBaseStepForDate(date: String, baseStep: Int) {
        prefs.edit().putInt(baseStepKey(date), baseStep).apply()
        firebaseDb.child("users")
            .child(userId)
            .child("baseSteps")
            .child(date)
            .setValue(baseStep)
    }

    fun syncStepsFromFirebase(lastSensorSteps: Int? = null, onComplete: (() -> Unit)? = null) {
        firebaseDb.child("users")
            .child(userId)
            .child("steps")
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach { child ->
                    val date = child.key ?: return@forEach
                    val steps = child.getValue(Int::class.java) ?: 0
                    prefs.edit().putInt(stepKey(date), steps).apply()
                }

                lastSensorSteps?.let { initializeBaseStep(today, it) }

                onComplete?.invoke()
            }
    }

    fun initializeBaseStep(today: String, totalSensorSteps: Int) {
        val syncedSteps = getStepsForDate(today)
        val baseStep = totalSensorSteps - syncedSteps
        saveBaseStepForDate(today, baseStep)
    }

    fun clearLocalData() {
        prefs.edit().clear().apply()
    }

}
