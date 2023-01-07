package com.szalik.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.Player
import com.szalik.logic.entertainment.enums.UserMode
import com.szalik.ui.theme.SzalikTheme
import kotlin.random.Random

val dbRef = DatabaseConnection.getDatabase().getReference("lobbies")
var exists by mutableStateOf<Boolean?>(null)

@Composable
fun CreateGameScreen(navController: NavController) {
    val context = LocalContext.current
    var lobbyId by remember {
        mutableStateOf(getNewLobbyId())
    }
    var clicked by remember {
        mutableStateOf(false)
    }
    var transferred by remember {
        mutableStateOf(false)
    }
    var name by remember {
        mutableStateOf("")
    }

    if (clicked) {
        if (exists == false && !transferred) {
            GameFlow.isHost = true
            val playerId = dbRef.push().key
            val player = Player(name = "Jasio", id = playerId!!)
            GameFlow.thisPlayerId = player.id
            GameFlow.setLobbyId(lobbyId)

            dbRef.child(lobbyId).child("state").setValue("WAIT")
            dbRef.child(lobbyId).child("players").child(playerId).setValue(player)
            transferred = true
            navController.navigate(Screen.LobbyScreen.withArgs(lobbyId, UserMode.HOST.name))
        } else if (exists == true){
            lobbyId = getNewLobbyId()
            checkIfLobbyExists(lobbyId)
            exists = null
        } else {
            exists = null
        }
    }


    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize(), Arrangement.Center) {
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

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name != "") {
                            if (GameFlow.testMode) {
                                GameFlow.isHost = true
                                val playerId = dbRef.push().key
                                val player = Player(name = name, id = playerId!!)
                                GameFlow.thisPlayerId = player.id
                                dbRef.child("123456").setValue(null)
                                GameFlow.setLobbyId("123456")
                                dbRef.child("123456").child("state").setValue("WAIT")
                                dbRef.child("123456").child("players").child(playerId).setValue(player)
                                navController.navigate(Screen.LobbyScreen.withArgs("123456", UserMode.HOST.name))
                            } else {
                                checkIfLobbyExists(lobbyId)
                                clicked = true
                            }
                        } else {
                            Toast.makeText(context, "Podaj swoją nazwę!", Toast.LENGTH_LONG).show()
                        }

                    },
                    modifier = Modifier
                        .padding(40.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Utwórz nową grę",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun getNewLobbyId(): String {
    val random = Random.nextInt(100000, 999999).toString()
    return random
}

private fun checkIfLobbyExists(id: String) {
    dbRef.addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.v("CREATE_GAME_SCREEN", "Check if game $id exists")
            exists = snapshot.hasChild(id)
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })
}