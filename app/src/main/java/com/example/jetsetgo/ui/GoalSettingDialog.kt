package com.example.jetsetgo.ui

import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun GoalSettingDialog(
    currentGoal: Int,
    onGoalChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var goalInput by remember { mutableStateOf(currentGoal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val newGoal = goalInput.toIntOrNull()
                when {
                    newGoal != null && newGoal in 1000..50000 -> {
                        onGoalChange(newGoal)
                        onDismiss()
                    }
                    newGoal != null && newGoal < 1000 -> {
                        Toast.makeText(context, "Goal must be at least 1000 steps", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, "Invalid input. Please enter a valid number.", Toast.LENGTH_LONG).show()
                    }
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Set Daily Goal") },
        text = {
            OutlinedTextField(
                value = goalInput,
                onValueChange = { goalInput = it },
                label = { Text("Step Goal") },
                singleLine = true
            )
        }
    )
}
