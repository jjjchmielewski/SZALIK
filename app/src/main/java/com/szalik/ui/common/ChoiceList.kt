package com.szalik.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.enums.Role
import com.szalik.logic.entertainment.enums.VotingMode
import com.szalik.ui.screens.dbRef

@Composable
fun ChoiceList(mode: VotingMode? = null) {
    val dbRef = DatabaseConnection.getDatabase().getReference("lobbies")
    var filteredList = GameFlow.listOfPlayers.filter { it.id != GameFlow.thisPlayerId }
    if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.BODYGUARD && GameFlow.isNight) {
        filteredList = filteredList.filter { it.id != GameFlow.lastProtectedPlayerId }
    }

    LazyColumn {
        itemsIndexed(filteredList) { _, player ->
            Text(text = player.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clickable {
                        when (mode) {
                            VotingMode.HANG -> {

                            }
                            VotingMode.SEARCH -> {
                                dbRef
                                    .child(GameFlow.getLobbyId())
                                    .child("playerToSearch")
                                    .setValue(player.id)
                                GameFlow.showSearchChoiceList = false
                            }
                            VotingMode.DUEL -> {
                                dbRef
                                    .child(GameFlow.getLobbyId())
                                    .child("dueledPlayer")
                                    .setValue(player.id)
                                dbRef
                                    .child(GameFlow.getLobbyId())
                                    .child("duelingPlayer")
                                    .setValue(GameFlow.thisPlayerId)
                                GameFlow.showDuelChoiceList = false
                            }
                            null -> {
                                dbRef
                                    .child(GameFlow.getLobbyId())
                                    .child("chosenPlayer")
                                    .setValue(player.id)
                                GameFlow.showChoiceList = false
                            }
                        }
                    })
        }
    }
}