package com.example.appnebula.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de Colores (Modo Oscuro, basado en el diseño)
private val DarkColorScheme = darkColorScheme(
    primary = AccentPink,              // Botones principales ("Explorar", "Reservar")
    onPrimary = DarkPurple,            // Texto sobre esos botones

    secondary = MidPurple,             // Banner "Nuevo Origen"
    onSecondary = TextLight,           // Texto sobre el banner

    tertiary = LightPurple,            // Fondo de Cards de Categoría
    onTertiary = DarkPurple,            // Icono/Texto en Cards de Categoría

    background = DarkPurple,           // Fondo general de la app
    onBackground = TextMuted,          // Texto "Hola, amante...", "Nuestras Categorías"

    surface = DarkPurple,              // Fondo de la TopAppBar (igual que el fondo)
    onSurface = TextMuted,             // Texto y iconos de la TopAppBar

    surfaceVariant = MidPurple,        // Fondo de la Bottom Nav Bar
    onSurfaceVariant = IconMuted       // Iconos inactivos de la Nav Bar
)

// Dejamos un LightColorScheme por si acaso, pero no lo usaremos
private val LightColorScheme = lightColorScheme(
    primary = AccentPink,
    onPrimary = DarkPurple,
    secondary = MidPurple,
    onSecondary = TextLight,
    tertiary = LightPurple,
    onTertiary = DarkPurple,
    background = Color(0xFFFBF7FF),
    onBackground = DarkPurple,
    surface = Color(0xFFFBF7FF),
    onSurface = DarkPurple,
    surfaceVariant = Color(0xFFEDE0F7),
    onSurfaceVariant = MidPurple
)

@Composable
fun AppNebulaTheme(
    // Forzamos el tema oscuro para que coincida con el diseño
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Desactivamos colores dinámicos
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb() // Barra de estado
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Usa la tipografía que ya tenías
        content = content
    )
}