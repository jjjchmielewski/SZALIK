package com.szalik

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.szalik.ui.screens.BusinessScreen
import com.szalik.ui.screens.GameScreen
import com.szalik.ui.screens.MainScreen
import com.szalik.ui.screens.*

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.GameScreen.route) {
            GameScreen(navController = navController)
        }
        composable(route = Screen.BusinessScreen.route) {
            BusinessScreen(navController = navController)
        }
        composable(route = Screen.CreateGameScreen.route) {
            CreateGameScreen(navController = navController)
        }
        composable(route = Screen.JoinGameScreen.route) {
            JoinGameScreen(navController = navController)
        }
        composable(route = Screen.CreateMeetingScreen.route) {
            CreateMeetingScreen(navController = navController)
        }
        composable(route = Screen.JoinMeetingScreen.route) {
            JoinMeetingScreen(navController = navController)
        }
        composable(
            route = Screen.LobbyScreen.route + "/{lobbyId}/{mode}",
            arguments = listOf(
                navArgument("lobbyId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("mode") {
                    type = NavType.StringType
                    nullable = false
                }
            )
            ) {
            LobbyScreen(
                navController = navController,
                lobbyId = it.arguments?.getString("lobbyId")!!,
                mode = it.arguments?.getString("mode")!!
            )
        }
        composable(route = Screen.CardScreen.route) {
            CardScreen()
        }
    }
}