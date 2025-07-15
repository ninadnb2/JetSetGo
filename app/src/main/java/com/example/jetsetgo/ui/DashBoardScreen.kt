package com.example.jetsetgo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    stepCount: Int,
    goal: Int,
    onSetGoal: (Int) -> Unit,
    onViewWeeklyClicked: () -> Unit,
    onLogout: () -> Unit
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    val stepStatsSummary = remember(stepCount, goal) {
        computeStepStats(stepCount, goal)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome back!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        StepStatsScreen(summary = stepStatsSummary)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onViewWeeklyClicked) {
            Text("View Weekly Progress")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showGoalDialog = true }) {
            Text("Set Daily Goal")
        }

        Button(onClick = onLogout, modifier = Modifier.padding(8.dp)) {
            Text("Logout")
        }
    }

    if (showGoalDialog) {
        GoalSettingDialog(currentGoal = goal, onGoalChange = {
            onSetGoal(it)
            showGoalDialog = false
        }, onDismiss = { showGoalDialog = false })
    }
}

