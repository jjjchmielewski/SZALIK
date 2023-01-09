package com.szalik.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.szalik.logic.business.MeetingFlow
import com.szalik.logic.common.TTSEngine
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.enums.MeetingMode
import com.szalik.logic.entertainment.enums.UserMode
import com.szalik.ui.theme.SzalikTheme

@Composable
fun LobbyScreen(navController: NavController, lobbyId: String, userMode: String, meetingMode: String) {
    val players = remember { GameFlow.listOfPlayers }
    val users = remember {
        MeetingFlow.listOfUsers
    }
    val context = LocalContext.current

    var init by remember {
        mutableStateOf(false)
    }

    if (!init) {
        TTSEngine.getTTS(context)
        init = true
    }

    Log.v("LOBBY_SCREEN", "Recomposing")
    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp), Arrangement.SpaceBetween, Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (meetingMode == MeetingMode.ENTERTAINMENT.name) "Kod gry: $lobbyId" else "Kod spotkania: $lobbyId",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    if (meetingMode == MeetingMode.ENTERTAINMENT.name) {
                        for(player in players) {
                            Row(Modifier.fillMaxWidth()) {
                                Text(
                                    text = player.name,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    } else {
                        for(user in users) {
                            Row(Modifier.fillMaxWidth()) {
                                Text(
                                    text = user.name,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
                if (userMode == UserMode.HOST.name) {
                    Button(
                        onClick = {
                            if (meetingMode == MeetingMode.ENTERTAINMENT.name) {
                                if (players.size < 6) {
                                    Toast.makeText(context, "Zbyt mało graczy!", Toast.LENGTH_LONG).show()
                                } else {
                                    GameFlow.prepareGameByHost()
                                    GameFlow.playerInGame = true
                                    navController.navigate(Screen.CardScreen.route)
                                }
                            } else {
                                if (users.size < 2) {
                                    Toast.makeText(context, "Potrzeba co najmniej 2 uczestników!", Toast.LENGTH_LONG).show()
                                } else {
                                    MeetingFlow.prepareMeetingByHost()
                                    navController.navigate(Screen.PrepareMeetingScreen.route)
                                }
                            }

                        },
                        Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = if (meetingMode == MeetingMode.ENTERTAINMENT.name) "Rozpocznij grę" else "Rozpocznij spotkanie",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    if (meetingMode == MeetingMode.ENTERTAINMENT.name) {
                        if (GameFlow.status == "STARTED" && !GameFlow.playerInGame) {
                            GameFlow.playerInGame = true
                            GameFlow.prepareGameByGuest()
                            navController.navigate(Screen.CardScreen.route)
                        } else {
                            Text(
                                text = "Oczekiwanie na rozpoczęcie gry",
                                color = MaterialTheme.colors.onPrimary,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        if (MeetingFlow.status == "STARTED" && !MeetingFlow.userInMeeting) {
                            MeetingFlow.userInMeeting = true
                            MeetingFlow.startAsGuest()
                            navController.navigate(Screen.MeetingScreen.route)
                        } else {
                            Text(
                                text = "Oczekiwanie na rozpoczęcie spotkania",
                                color = MaterialTheme.colors.onPrimary,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}