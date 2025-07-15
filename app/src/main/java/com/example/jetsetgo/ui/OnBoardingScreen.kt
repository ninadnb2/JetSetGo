package com.example.jetsetgo.ui

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.jetsetgo.R

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val slides = listOf(
        OnboardingData("Track Steps", "Count your daily steps easily", R.raw.walking_onboarding),
        OnboardingData("Achieve Goals", "Set and achieve step goals", R.raw.trophy_onboarding),
        OnboardingData("Stay Healthy", "Build healthy walking habits", R.raw.fit_onboarding)
    )

    val pagerState = rememberPagerState(initialPage = 0) { slides.size }
    val context = LocalContext.current

    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val isLastPage = currentPage == slides.lastIndex
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                OnboardingItem(slides[page])
            }

            Spacer(modifier = Modifier.height(16.dp))

            PagerIndicator(
                totalPages = slides.size,
                currentPage = currentPage
            )
        }

        AnimatedVisibility(
            visible = isLastPage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = {
                    markOnboardingComplete(context)
                    onFinish()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Started")
            }
        }
    }
}

@Composable
fun PagerIndicator(totalPages: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(totalPages) { index ->
            val color =
                if (index == currentPage) MaterialTheme.colorScheme.primary else Color.Gray

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            if (index != totalPages - 1) {
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}

@Composable
fun OnboardingItem(data: OnboardingData) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(data.animation))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = data.title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = data.desc,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

data class OnboardingData(val title: String, val desc: String, val animation: Int)

fun isOnboardingComplete(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("jetsetgo_prefs", Context.MODE_PRIVATE)
    return sharedPref.getBoolean("onboarding_complete", false)
}

fun markOnboardingComplete(context: Context) {
    val sharedPref = context.getSharedPreferences("jetsetgo_prefs", Context.MODE_PRIVATE)
    sharedPref.edit().putBoolean("onboarding_complete", true).apply()
}
