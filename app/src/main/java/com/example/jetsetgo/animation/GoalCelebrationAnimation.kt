package com.example.jetsetgo.animation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.*
import com.example.jetsetgo.R

@Composable
fun GoalCelebrationAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.celebration))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 2
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}
