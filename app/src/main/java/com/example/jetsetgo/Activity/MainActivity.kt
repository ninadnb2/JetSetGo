package com.example.jetsetgo.Activity

import android.content.Context
import android.hardware.*
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetsetgo.notification.ReminderScheduler
import com.example.jetsetgo.repository.StepRepository
import com.example.jetsetgo.ui.DashboardScreen
import com.example.jetsetgo.ui.LoginScreen
import com.example.jetsetgo.ui.OnboardingScreen
import com.example.jetsetgo.ui.WeeklyStatsScreen
import com.example.jetsetgo.ui.computeWeeklyStats
import com.example.jetsetgo.ui.isOnboardingComplete
import com.example.jetsetgo.ui.theme.JetSetGoTheme
import com.example.jetsetgo.viewmodel.StepViewModel
import com.example.jetsetgo.worker.MidnightResetScheduler
import com.google.accompanist.permissions.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private val viewModel: StepViewModel by viewModels()
    private lateinit var stepRepository: StepRepository
    private var lastSensorSteps: Int? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var verificationId: String? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        setupGoogleSignIn()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepRepository = StepRepository(applicationContext)
        MidnightResetScheduler.scheduleMidnightReset(applicationContext)

        setContent {
            JetSetGoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RequestNotificationPermission()
                    val user = auth.currentUser
                    if (user == null) {
                        var showOnboarding by remember { mutableStateOf(!isOnboardingComplete(this)) }

                        if (showOnboarding) {
                            OnboardingScreen(onFinish = {
                                showOnboarding = false
                            })
                        } else {
                            LoginScreen(onGoogleLogin = { signInWithGoogle() },
                                onPhoneLogin = { phone ->
                                    startPhoneVerification(phone)
                                })
                        }
                    } else {
                        ensureMidnightReset()
                        var isSynced by remember { mutableStateOf(false) }

                        LaunchedEffect(Unit) {
                            stepRepository.syncStepsFromFirebase(lastSensorSteps) {
                                val today = stepRepository.today
                                val existingSteps = stepRepository.getStepsForDate(today)
                                viewModel.updateSteps(existingSteps)
                                sensor?.let {
                                    sensorManager.registerListener(
                                        this@MainActivity, it, SensorManager.SENSOR_DELAY_NORMAL
                                    )
                                }
                                isSynced = true
                            }
                        }

                        if (isSynced) {
                            val stepCount by viewModel.stepCount.collectAsState()
                            val goal by viewModel.goal.collectAsState()
                            var currentScreen by remember { mutableStateOf("dashboard") }

                            val permissionState =
                                rememberPermissionState(android.Manifest.permission.ACTIVITY_RECOGNITION)

                            LaunchedEffect(Unit) {
                                permissionState.launchPermissionRequest()
                            }

                            val weeklyData =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) stepRepository.getWeeklyStepData()
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
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
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
                DashboardScreen(stepCount = stepCount,
                    goal = goal,
                    onSetGoal = onUpdateGoal,
                    onViewWeeklyClicked = onNavigateToWeekly,
                    onLogout = {
                        auth.signOut()
                        googleSignInClient.signOut()
                        recreate()
                        stepRepository.clearLocalData()
                    })
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

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.jetsetgo.R.string.default_web_client_id))
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Google Login Successful!", Toast.LENGTH_SHORT).show()
                        recreate()
                    } else {
                        Toast.makeText(this, "Google Login Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun startPhoneVerification(phone: String) {
        val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneCredential(credential)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(
                        this@MainActivity, "Verification Failed: ${p0.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(
                    verificationId: String, token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@MainActivity.verificationId = verificationId
                    Toast.makeText(this@MainActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String) {
        verificationId?.let {
            val credential = PhoneAuthProvider.getCredential(it, otp)
            signInWithPhoneCredential(credential)
        }
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Phone Login Successful!", Toast.LENGTH_SHORT).show()
                recreate()
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
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
                lastSensorSteps = totalSteps

                val today = stepRepository.today
                var baseStep = stepRepository.getBaseStepForDate(today)
                stepRepository.saveLastSensorTotalSteps(totalSteps)

                if (baseStep == -1) {
                    val syncedSteps = stepRepository.getStepsForDate(today)
                    baseStep = totalSteps - syncedSteps
                    stepRepository.saveBaseStepForDate(today, baseStep)
                }

                val todaySteps = maxOf(0, totalSteps - baseStep)
                viewModel.updateSteps(todaySteps)
                stepRepository.saveStepsForDate(today, todaySteps)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

            LaunchedEffect(Unit) {
                if (!permissionState.status.isGranted) {
                    permissionState.launchPermissionRequest()
                }
            }
        }
    }

    fun ensureMidnightReset() {
        val today = LocalDate.now().toString()
        val savedSteps = stepRepository.getStepsForDate(today)
        val baseStep = stepRepository.getBaseStepForDate(today)

        if (savedSteps == 0 && baseStep == -1) {
            val lastSensorSteps = stepRepository.getLastSensorTotalSteps()
            stepRepository.saveBaseStepForDate(today, lastSensorSteps)
            stepRepository.saveStepsForDate(today, 0)
        }
    }

}
