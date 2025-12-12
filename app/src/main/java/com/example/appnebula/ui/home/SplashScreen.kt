package com.example.appnebula.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appnebula.BottomNavItem
import com.example.appnebula.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimFondo by animateFloatAsState(
        targetValue = if (startAnimation) 0.7f else 0f,
        animationSpec = tween(
            durationMillis = 2000
        )
    )
    val scaleAnimFondo by animateFloatAsState(
        targetValue = if (startAnimation) 1.05f else 1f,
        animationSpec = tween(
            durationMillis = 3000
        )
    )

    val alphaAnimLogo by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            delayMillis = 500
        )
    )

    val alphaAnimTexto by animateFloatAsState(
        targetValue = if (startAnimation) 0.9f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 1200
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000L)

        navController.navigate(BottomNavItem.Home.route) {
            popUpTo("splash") {
                inclusive = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.nebula_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleAnimFondo)
                .alpha(alphaAnimFondo)
        )

        Text(
            text = "Bienvenido a Nébula Café",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -230.dp)
                .padding(horizontal = 24.dp)
                .alpha(alphaAnimTexto)
        )

        Image(
            painter = painterResource(id = R.drawable.nebula),
            contentDescription = "Logo Nébula Café",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -150.dp)
                .size(230.dp)
                .alpha(alphaAnimLogo)
        )
    }
}