package com.example.checkers.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.checkers.ui.theme.screens.SplashScreen
import com.example.checkers.ui.theme.screens.home.HomeScreen
import com.example.checkers.ui.theme.screens.login.LoginScreen
import com.example.checkers.ui.theme.screens.register.Registerscreen
import com.example.checkers.ui.theme.screens.students.AddstudentScreen

@Composable
fun AppNavHost(navController: NavHostController= rememberNavController(),startDestination: String= ROUTE_SPLASH){
    NavHost (navController=navController,startDestination=startDestination){
        composable(ROUTE_SPLASH) { SplashScreen {
            navController.navigate(ROUTE_REGISTER){
                popUpTo(ROUTE_SPLASH){inclusive=true}}} }
        composable(ROUTE_REGISTER) { Registerscreen(navController)  }
        composable (ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_HOME) { HomeScreen(navController) }
        composable(ROUTE_ADD_STUDENT) { AddstudentScreen(navController) }
    }
}