package com.szalik.ui.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.szalik.logic.common.UserMode
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.Player
import com.szalik.ui.theme.SzalikTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JoinGameScreen(navController: NavController) {
    var gameId by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    var exists: Boolean? = null
    var gameStatus = ""
    val dbRef = DatabaseConnection.getDatabase().getReference("lobbies")

    dbRef.addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.v("JOIN_GAME_SCREEN + ${System.identityHashCode(this)}", "Check if game $gameId exists")
            exists = snapshot.hasChild(gameId)
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

    dbRef.child(gameId).child("state").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.i("JOIN_GAME_SCREEN + ${System.identityHashCode(this)}", "Status is ${snapshot.value}!")
            if (snapshot.value  != null)
                gameStatus = snapshot.value as String
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

    if (GameFlow.testMode) {
        gameId = "123456"
    }

    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize(), Arrangement.Top) {
                Spacer(modifier = Modifier.fillMaxHeight(0.2f))

                TextField(
                    value = gameId,
                    placeholder = {
                        Text("Kod gry")
                    },
                    onValueChange = {
                        gameId = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = name,
                    placeholder = {
                        Text(text = "Nazwa gracza")
                    },
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (gameId != "" && gameId.length == 6) {
                            if (name != "") {
                                loop@ while (true) {
                                    when (exists) {
                                        null -> {
                                            continue@loop
                                        }
                                        true -> {
                                            if (gameStatus == "WAIT") {
                                                GameFlow.setLobbyId(gameId)
                                                val playerId = dbRef.push().key
                                                val player = Player(name = name, id = playerId!!)
                                                GameFlow.thisPlayerId = player.id
                                                dbRef.child(gameId).child("players").child(playerId)
                                                    .setValue(player)
                                                navController.navigate(
                                                    Screen.LobbyScreen.withArgs(
                                                        gameId,
                                                        UserMode.GUEST.name
                                                    )
                                                )
                                                break@loop
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Ta gra już się rozpoczęła!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                break@loop
                                            }
                                        }
                                        false -> {
                                            Toast.makeText(
                                                context,
                                                "Nie znaleziono gry dla podanego kodu",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Podaj swoją nazwę!", Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Niepoprawny format kodu gry!",
                                Toast.LENGTH_LONG
                            ).show()
                        }


                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Dołącz do gry",
                        color = Color.Black,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}