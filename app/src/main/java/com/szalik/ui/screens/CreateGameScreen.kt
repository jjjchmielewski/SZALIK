package com.szalik.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.szalik.logic.common.UserMode
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.Player
import com.szalik.ui.theme.SzalikTheme
import kotlin.random.Random

@Composable
fun CreateGameScreen(navController: NavController) {
    var exists: Boolean? = null
    val dbRef = DatabaseConnection.getDatabase().getReference("lobbies")
    var lobbyId by remember { mutableStateOf(getNewLobbyId())}

    dbRef.addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.v("CREATE_GAME_SCREEN", "Check if game $lobbyId exists")
            exists = snapshot.hasChild(lobbyId)
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize(), Arrangement.Center) {
                Button(
                    onClick = {
                        loop@while(true) {
                            when (exists) {
                                null -> {
                                    continue@loop
                                }
                                true -> {
                                    lobbyId = getNewLobbyId()
                                }
                                false -> {
                                    break@loop
                                }
                            }
                        }

                        val playerId = dbRef.push().key
                        val player = Player(name = "Jasio", id = playerId!!)
                        GameFlow.thisPlayerId = player.id
                        GameFlow.setLobbyId(lobbyId)

                        dbRef.child(lobbyId).child("state").setValue("WAIT")
                        dbRef.child(lobbyId).child("players").child(playerId).setValue(player)
                        for(i in 5 downTo 1) {
                            val dummyId = dbRef.push().key
                            val dummy = Player("dummy$i", dummyId!!)
                            dbRef.child(lobbyId).child("players").child(dummyId).setValue(dummy)
                        }
                        navController.navigate(Screen.LobbyScreen.withArgs(lobbyId, UserMode.HOST.name))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Utwórz nową grę")
                }
            }
        }
    }
}

private fun getNewLobbyId(): String {
    return Random.nextInt(100000, 999999).toString()
}