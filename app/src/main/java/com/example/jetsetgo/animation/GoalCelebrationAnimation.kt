package com.example.jetsetgo.animation

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.airbnb.lottie.compose.*
import com.example.jetsetgo.R

@Composable
fun GoalCelebrationAnimation(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.celebration))
    val animatable = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        if (composition != null) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(1000,
                VibrationEffect.DEFAULT_AMPLITUDE))

            animatable.animate(
                composition = composition,
                iterations = 2
            )
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { animatable.progress },
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}

