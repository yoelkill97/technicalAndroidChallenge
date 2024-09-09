package com.example.technicalchallenge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.technicalchallenge.presentation.SplashScreen

import com.example.technicalchallenge.presentation.login.SingInPage
import com.example.technicalchallenge.presentation.register.RegisterPage


@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.route
    ) {
        composable(route = Screens.SplashScreen.route) {
            SplashScreen(onNavigateToLogin = {
                navController.navigate(Screens.SignInScreen.route) {
                    popUpTo(it.destination.id) {
                        inclusive = true
                    }
                }
            })
        }

        composable(route = Screens.SignInScreen.route) {
            SingInPage(onNavigateToRegister = {
                navController.navigate(Screens.RegisterScreen.route){
                    popUpTo(it.destination.id) {
                        inclusive = true
                    }
                }
            })

        }
        composable(route = Screens.RegisterScreen.route) {
            RegisterPage()
        }
    }

}