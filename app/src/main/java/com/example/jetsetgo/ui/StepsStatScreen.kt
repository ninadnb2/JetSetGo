package com.example.jetsetgo.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.jetsetgo.animation.GoalCelebrationAnimation
import com.example.jetsetgo.data.StepStatsSummary
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

@Composable
fun StepStatsScreen(
    summary: StepStatsSummary
) {
    val animatedSteps = animateIntAsState(targetValue = summary.stepCount, label = "").value
    val cardModifier = Modifier.fillMaxWidth()

    var showCelebration by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = summary.stepCount >= summary.goal) {
        if (summary.stepCount >= summary.goal) {
            showCelebration = true
            delay(10000)
            showCelebration = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(modifier = cardModifier) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$animatedSteps steps",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        LinearProgressIndicator(
                            progress = { summary.progress },
                            modifier = Modifier.fillMaxSize(),
                            color = summary.progressColor,
                            trackColor = Color.Transparent
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${summary.rawProgressPercent}% of daily goal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = summary.progressColor
                    )
                }
            }

            Card(modifier = cardModifier) {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = summary.motivationText,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            Card(modifier = cardModifier) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Distance", fontWeight = FontWeight.Medium)
                        Text("%.2f km".format(summary.distanceInKm), fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Calories", fontWeight = FontWeight.Medium)
                        Text("%.1f kcal".format(summary.caloriesBurned), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        if (showCelebration) {
            GoalCelebrationAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}




fun computeStepStats(stepCount: Int, goal: Int): StepStatsSummary {
    val progress = (stepCount / goal.toFloat()).coerceAtMost(1f)
    val rawProgressPercent = ((stepCount / goal.toFloat()) * 100).toInt()
    val distanceInKm = stepCount * 0.78 / 1000
    val caloriesBurned = stepCount * 0.04

    val motivationText = when {
        stepCount == 0 -> "Let's get moving! 🚶‍♂️"
        stepCount < goal / 2 -> "You're off to a great start 💪"
        stepCount < goal -> "Almost there! Just a little more 🏃"
        stepCount in goal..(goal * 1.3).toInt() -> "Goal smashed! You're on fire 🔥"
        else -> "Goal is already reached. You need to take rest 🧘"
    }

    val progressColor = when {
        stepCount == goal -> Color(0xFF58A803)
        stepCount > goal -> Color(0xFFFF7700)
        stepCount >= goal / 2 -> Color(0xFFCCB701)
        else -> Color(0xFF008DFF)
    }

    return StepStatsSummary(
        stepCount = stepCount,
        goal = goal,
        progress = progress,
        rawProgressPercent = rawProgressPercent,
        distanceInKm = distanceInKm,
        caloriesBurned = caloriesBurned,
        motivationText = motivationText,
        progressColor = progressColor
    )
}



@Preview(showBackground = true, apiLevel = 33)
@Composable
fun PreviewStepStatsScreen() {
    val summary = computeStepStats(stepCount = 1000, goal = 1000)
    StepStatsScreen(summary = summary)
}

