package com.example.jetsetgo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.jetsetgo.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StepViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = GoalRepository(application)
    private val _stepCount = MutableStateFlow(0)
    private val _goal = MutableStateFlow(repo.getGoal())

    val stepCount: StateFlow<Int> = _stepCount
    val goal: StateFlow<Int> = _goal

    fun updateSteps(count: Int) {
        _stepCount.value = count
    }

    fun updateGoal(newGoal: Int) {
        _goal.value = newGoal
        repo.saveGoal(newGoal)
    }
}
