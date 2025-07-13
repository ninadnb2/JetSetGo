package com.example.jetsetgo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsetgo.data.WeeklyStatsSummary
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.BarChartData.Bar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyStatsScreen(
    onBack: () -> Unit,
    summary: WeeklyStatsSummary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Weekly Stats") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp, 0.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Your Weekly Step Overview 🚶")

                Card{
                    BarChart(
                        barChartData = summary.barChartData,
                        modifier = Modifier.fillMaxSize().heightIn(150.dp,300.dp)
                            .padding(12.dp)
                    )
                }

                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("📅 Total Steps")
                        Text("${summary.totalSteps} steps")

                        Spacer(Modifier.height(12.dp))

                        Text("📈 Average Steps/Day")
                        Text("${summary.averageSteps} steps")
                    }
                }


                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("🏆 Most Active Day: ${summary.mostActiveDay?.first} (${summary.mostActiveDay?.second} steps)")
                        Text("😴 Least Active Day: ${summary.leastActiveDay?.first} (${summary.leastActiveDay?.second} steps)")
                        Text("📅 Days Goal Met: ${summary.daysGoalMet} / 7")
                        Text("🏷️ Badge: ${summary.badge}")
                    }
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
            ) {
                Text("Back to Dashboard")
            }
        }
    }
}

fun computeWeeklyStats(stepData: List<Pair<String, Int>>, dailyGoal: Int): WeeklyStatsSummary {
    val totalSteps = stepData.sumOf { it.second }
    val averageSteps = if (stepData.isNotEmpty()) totalSteps / stepData.size else 0
    val mostActive = stepData.maxByOrNull { it.second }
    val leastActive = stepData.minByOrNull { it.second }
    val daysGoalMet = stepData.count { it.second >= dailyGoal }

    val badge = when {
        daysGoalMet == 7 -> "🏅 Perfect Week!"
        daysGoalMet >= 5 -> "💪 Great Consistency!"
        daysGoalMet in 2..4 -> "🚶 Getting There!"
        else -> "⏳ Just Starting Out"
    }

    val nonZeroBars = stepData.filter { it.second > 0 }

    val bars = if (nonZeroBars.isEmpty()) {
        listOf(
            Bar(
                label = "No Data",
                value = 1f,
                color = Color.LightGray
            ) // dummy 1f bar to prevent crash
        )
    } else {
        stepData.map { (label, value) ->
            Bar(
                label = label,
                value = value.toFloat(),
                color = if (value >= dailyGoal) Color(0xFF4CAF50) else Color(0xFFFFC107)
            )
        }
    }
    return WeeklyStatsSummary(
        totalSteps = totalSteps,
        averageSteps = averageSteps,
        mostActiveDay = mostActive,
        leastActiveDay = leastActive,
        daysGoalMet = daysGoalMet,
        badge = badge,
        barChartData = BarChartData(bars)
    )
}


@Preview(apiLevel = 33, showBackground = true)
@Composable
fun PreviewWeeklyStatsScreen() {
    val steps = listOf(
        "Mon" to 4000,
        "Tue" to 5000,
        "Wed" to 8000,
        "Thu" to 6500,
        "Fri" to 9000,
        "Sat" to 1000,
        "Sun" to 7000
    )
    val summary = computeWeeklyStats(steps, dailyGoal = 8000)
    WeeklyStatsScreen(onBack = {}, summary = summary)
}

