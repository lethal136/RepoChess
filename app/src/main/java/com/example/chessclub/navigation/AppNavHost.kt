package com.example.chessclub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chessclub.ui.theme.screens.SplashScreen
import com.example.chessclub.ui.theme.screens.home.HomeScreen
import com.example.chessclub.ui.theme.screens.login.LoginScreen
import com.example.chessclub.ui.theme.screens.register.Registerscreen
import com.example.chessclub.ui.theme.screens.players.AddplayerScreen
import com.example.chessclub.ui.theme.screens.players.UpdatePlayerScreen
import com.example.chessclub.ui.theme.screens.players.ViewPlayers

@Composable
fun AppNavHost(navController: NavHostController= rememberNavController(),startDestination: String= ROUTE_SPLASH){
    NavHost (navController=navController,startDestination=startDestination){
        composable(ROUTE_SPLASH) { SplashScreen {
            navController.navigate(ROUTE_REGISTER){
                popUpTo(ROUTE_SPLASH){inclusive=true}}} }
        composable(ROUTE_REGISTER) { Registerscreen(navController)  }
        composable (ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_HOME) { HomeScreen(navController) }
        composable(ROUTE_ADD_PLAYER) { AddplayerScreen(navController) }
        composable(ROUTE_UPDATE_PLAYER) { UpdatePlayerScreen(navController) }
        composable(ROUTE_VIEW_PLAYERS) { ViewPlayers(navController) }
    }
}