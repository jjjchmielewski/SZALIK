package com.szalik.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.szalik.logic.entertainment.enums.UserMode
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.GameFlow.Companion.status
import com.szalik.ui.theme.SzalikTheme

@Composable
fun LobbyScreen(navController: NavController, lobbyId: String, mode: String) {
    val players = remember { GameFlow.listOfPlayers }
    val context = LocalContext.current

    Log.v("LOBBY_SCREEN", "Recomposing")
    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize(), Arrangement.SpaceBetween, Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kod gry: $lobbyId",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    for(player in players) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(text = player.name, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                }
                if (mode == UserMode.HOST.name) {
                    Button(
                        onClick = {
                            if (players.size < 6) {
                                Toast.makeText(context, "Zbyt mało graczy!", Toast.LENGTH_LONG).show()
                            } else {
                                GameFlow.prepareGameByHost()
                                GameFlow.playerInGame = true
                                navController.navigate(Screen.CardScreen.route)
                            }
                        },
                        Modifier.padding(10.dp)
                    ) {
                        Text("Rozpocznij grę")
                    }
                } else {
                    if (status == "STARTED" && !GameFlow.playerInGame) {
                        GameFlow.playerInGame = true
                        GameFlow.prepareGameByGuest()
                        navController.navigate(Screen.CardScreen.route)
                    } else {
                        Text(
                            text = "Oczekiwanie na rozpoczęcie gry",
                            color = Color.DarkGray,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }
        }
    }
}

