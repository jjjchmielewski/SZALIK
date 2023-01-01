package com.szalik.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.szalik.ui.common.Tile
import com.szalik.ui.screens.Screen
import com.szalik.ui.theme.SzalikTheme

@Composable
fun BusinessScreen(navController: NavController) {
    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize(), Arrangement.Center) {
                Spacer(modifier = Modifier.fillMaxHeight(0.2f))

                Row(Modifier.fillMaxWidth(), Arrangement.Center){
                    Text(
                        text = "Asystent spotkań",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.fillMaxHeight(0.2f))

                Row(
                    Modifier
                        .fillMaxWidth(),
                    Arrangement.SpaceEvenly
                ) {
                    Tile(
                        name = "Utwórz spotkanie",
                        color = Color.Gray,
                        navController = navController,
                        destination = Screen.CreateMeetingScreen.route
                    )
                    Tile(
                        name = "Dołącz do spotkania",
                        color = MaterialTheme.colors.primary,
                        navController = navController,
                        destination = Screen.JoinMeetingScreen.route
                    )
                }
            }
        }
    }
}