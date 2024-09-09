package com.example.technicalchallenge.presentation

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.technicalchallenge.R
import com.example.technicalchallenge.navigation.Screens
import com.example.technicalchallenge.ui.theme.TechnicalChallengeTheme
import kotlinx.coroutines.delay

@Preview(showSystemUi = true)
@Composable
fun SplashPreview(){
    TechnicalChallengeTheme{
        SplashScreen()
    }

}

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit = {}) {
    val animationDuration = 2000
    val animationDelay = 1000

    // Animación de escala
    val scaleAnimation by animateFloatAsState(
        targetValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            0.5f
        } else {

            1.2f
        },
        animationSpec = tween(durationMillis = animationDuration, delayMillis = animationDelay)
    )

    // Animación de opacidad
    val alphaAnimation by animateFloatAsState(
        targetValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            0.4f
        } else {

            0.8f
        },
        animationSpec = tween(durationMillis = animationDuration, delayMillis = animationDelay)
    )

    val backgroundColorAnimation by animateColorAsState(
        targetValue = Color(0xFFE0EFF4),
        animationSpec = tween(durationMillis = animationDuration, delayMillis = animationDelay)
    )

    val splashScreenImage = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_android))


    LaunchedEffect(key1 = true) {
        delay(animationDuration + animationDelay.toLong())
          onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColorAnimation),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = splashScreenImage,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .alpha(alphaAnimation)
                .scale(scaleAnimation)
        )
    }
}