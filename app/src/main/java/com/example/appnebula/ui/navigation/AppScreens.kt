package com.example.appnebula.navigation


sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")

    object RegisterScreen : AppScreens("register_screen")

    // Puedes añadir más pantallas aquí (ej. HomeScreen, ProfileScreen)
    // object HomeScreen : AppScreens("home_screen")
}