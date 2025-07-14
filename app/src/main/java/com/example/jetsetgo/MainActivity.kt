package com.example.jetsetgo

import android.content.Context
import android.hardware.*
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jetsetgo.notification.ReminderScheduler
import com.example.jetsetgo.repository.StepRepository
import com.example.jetsetgo.ui.DashboardScreen
import com.example.jetsetgo.ui.WeeklyStatsScreen
import com.example.jetsetgo.ui.computeWeeklyStats
import com.example.jetsetgo.ui.theme.JetSetGoTheme
import com.example.jetsetgo.viewmodel.StepViewModel
import com.google.accompanist.permissions.*

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private val viewModel: StepViewModel by viewModels()
    private lateinit var stepRepository: StepRepository

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepRepository = StepRepository(applicationContext)

        setContent {
            JetSetGoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val stepCount by viewModel.stepCount.collectAsState()
                    val goal by viewModel.goal.collectAsState()
                    var currentScreen by remember { mutableStateOf("dashboard") }

                    val permissionState = rememberPermissionState(android.Manifest.permission.ACTIVITY_RECOGNITION)

                    LaunchedEffect(Unit) {
                        permissionState.launchPermissionRequest()
                    }

                    val weeklyData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        stepRepository.getWeeklyStepData()
                    else emptyList()

                    JetSetGoApp(
                        stepCount = stepCount,
                        goal = goal,
                        weeklyData = weeklyData,
                        onUpdateGoal = { viewModel.updateGoal(it) },
                        onBack = { currentScreen = "dashboard" },
                        onNavigateToWeekly = { currentScreen = "weekly" },
                        isWeeklyScreen = currentScreen == "weekly",
                        requestPermission = { permissionState.launchPermissionRequest() },
                        isPermissionGranted = permissionState.status.isGranted,
                        shouldShowRationale = permissionState.status.shouldShowRationale
                    )

                    if (permissionState.status.isGranted) {
                        LaunchedEffect(Unit) {
                            ReminderScheduler.scheduleDailyReminder(applicationContext)
                        }
                    }
                }
            }
        }

        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    @Composable
    fun JetSetGoApp(
        stepCount: Int,
        goal: Int,
        weeklyData: List<Pair<String, Int>>,
        onUpdateGoal: (Int) -> Unit,
        onBack: () -> Unit,
        onNavigateToWeekly: () -> Unit,
        isWeeklyScreen: Boolean,
        requestPermission: () -> Unit,
        isPermissionGranted: Boolean,
        shouldShowRationale: Boolean
    ) {
        if (isPermissionGranted) {
            BackHandler(enabled = isWeeklyScreen) {
                onBack()
            }

            if (isWeeklyScreen) {
                val summary = computeWeeklyStats(weeklyData, goal)
                WeeklyStatsScreen(onBack = onBack, summary = summary)
            } else {
                DashboardScreen(
                    stepCount = stepCount,
                    goal = goal,
                    onSetGoal = onUpdateGoal,
                    onViewWeeklyClicked = onNavigateToWeekly
                )
            }
        } else if (shouldShowRationale) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = requestPermission) {
                    Text("Grant Permission")
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = it.values[0].toInt()
                val today = stepRepository.today

                val baseStep = stepRepository.getBaseStepForDate(today)

                if (baseStep == -1) {
                    stepRepository.saveBaseStepForDate(today, totalSteps)
                }

                val effectiveBase = if (baseStep == -1) totalSteps else baseStep
                val stepsToday = totalSteps - effectiveBase
                viewModel.updateSteps(stepsToday)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stepRepository.saveStepsForDate(today, stepsToday)
                }
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
