package com.example.technicalchallenge.navigation

sealed class Screens(val route: String) {
    object SplashScreen : Screens(route = "splash_screen")
    object SignInScreen : Screens(route = "SignIn_Screen")
    object RegisterScreen : Screens(route = "Register_Screen")

}