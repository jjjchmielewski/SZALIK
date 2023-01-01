package com.szalik.ui.screens

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object GameScreen : Screen("game_screen")
    object BusinessScreen : Screen("business_screen")
    object CreateGameScreen : Screen("create_game_screen")
    object JoinGameScreen : Screen("join_game_screen")
    object CreateMeetingScreen : Screen("create_meeting_screen")
    object JoinMeetingScreen : Screen("join_meeting_screen")
    object LobbyScreen : Screen("lobby_screen")
    object CardScreen : Screen("card_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}