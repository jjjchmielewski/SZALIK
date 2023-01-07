package com.szalik.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.ServerValue
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.enums.Role
import com.szalik.logic.entertainment.enums.VotingMode

@Composable
fun Voting(mode: VotingMode) {
    val dbRef = DatabaseConnection.getDatabase().getReference("lobbies")
    var multiplier = 1L

    if ((GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.BLACKMAILER && GameFlow.listOfPlayers.find { it.card?.isBlackmailed == true } != null) || (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.SEDUCER && GameFlow.listOfPlayers.find { it.card?.isSeduced == true } != null)) {
        multiplier = 2L
    }

    when (mode) {
        VotingMode.HANG -> {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Czy chcesz powiesić ${GameFlow.listOfPlayers.find { it.id == GameFlow.playerToSearchId }?.name}?",
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.isSeduced == true) {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Zostałeś uwiedziony, twój głos będzie taki sam jak Uwodziciela",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    dbRef
                        .child(GameFlow.getLobbyId())
                        .child("voted")
                        .setValue(ServerValue.increment(1))
                    GameFlow.showVoting = false
                }) {
                    Text(
                        text = "Kontynuuj",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.isBlackmailed == true) {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Jesteś szantażowany, twój głos będzie taki sam jak Szantażysty",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    dbRef
                        .child(GameFlow.getLobbyId())
                        .child("voted")
                        .setValue(ServerValue.increment(1))
                    GameFlow.showVoting = false
                }) {
                    Text(
                        text = "Kontynuuj",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voting")
                            .setValue(ServerValue.increment(-1 * multiplier))
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false

                    }) {
                        Text(
                            text = "Nie",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voting")
                            .setValue(ServerValue.increment(1 * multiplier))
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false
                    }) {
                        Text(
                            text = "Tak",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        VotingMode.SEARCH -> {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Czy chcesz przeszukać ${GameFlow.listOfPlayers.find { it.id == GameFlow.playerToSearchId }?.name}?",
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.isSeduced == true) {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Zostałeś uwiedziony, twój głos będzie taki sam jak Uwodziciela",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    dbRef
                        .child(GameFlow.getLobbyId())
                        .child("voted")
                        .setValue(ServerValue.increment(1))
                    GameFlow.showVoting = false
                }) {
                    Text(
                        text = "Kontynuuj",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.isBlackmailed == true) {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Jesteś szantażowany, twój głos będzie taki sam jak Szantażysty",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    dbRef
                        .child(GameFlow.getLobbyId())
                        .child("voted")
                        .setValue(ServerValue.increment(1))
                    GameFlow.showVoting = false
                }) {
                    Text(
                        text = "Kontynuuj",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voting")
                            .setValue(ServerValue.increment(-1 * multiplier))
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false
                    }) {
                        Text(
                            text = "Nie",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voting")
                            .setValue(ServerValue.increment(1 * multiplier))
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false
                    }) {
                        Text(
                            text = "Tak",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        VotingMode.DUEL -> {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Kto powinien zwyciężyć pojedynek?",
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
            if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.isSeduced == true) {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Zostałeś uwiedziony, twój głos będzie taki sam jak Uwodziciela",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    dbRef
                        .child(GameFlow.getLobbyId())
                        .child("voted")
                        .setValue(ServerValue.increment(1))
                    GameFlow.showVoting = false
                }) {
                    Text(
                        text = "Kontynuuj",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.isBlackmailed == true) {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Jesteś szantażowany, twój głos będzie taki sam jak Szantażysty",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    dbRef
                        .child(GameFlow.getLobbyId())
                        .child("voted")
                        .setValue(ServerValue.increment(1))
                    GameFlow.showVoting = false
                }) {
                    Text(
                        text = "Kontynuuj",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voting")
                            .setValue(ServerValue.increment(1 * multiplier))
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false
                    }) {
                        Text(
                            text = GameFlow.listOfPlayers.find { it.id == GameFlow.duelingPlayerId }?.name!!,
                            textAlign = TextAlign.Center,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voting")
                            .setValue(ServerValue.increment(-1 * multiplier))
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false
                    }) {
                        Text(
                            text = GameFlow.listOfPlayers.find { it.id == GameFlow.dueledPlayerId }?.name!!,
                            textAlign = TextAlign.Center,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false
                    }) {
                        Text(
                            text = "Wstrzymaj się od głosu",
                            textAlign = TextAlign.Center,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.JUDGE && GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.actionsLeftCounter != 0) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Możesz skorzystać ze swojej zdolności i zadecydować o wyniku pojedynku:",
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voting")
                            .setValue(ServerValue.increment(150))
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false
                    }) {
                        Text(
                            text = "Wygrana ${GameFlow.listOfPlayers.find { it.id == GameFlow.duelingPlayerId }?.name!!}",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(onClick = {
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voting")
                            .setValue(ServerValue.increment(-150))
                        dbRef
                            .child(GameFlow.getLobbyId())
                            .child("voted")
                            .setValue(ServerValue.increment(1))
                        GameFlow.showVoting = false
                    }) {
                        Text(
                            text = "Wygrana ${GameFlow.listOfPlayers.find { it.id == GameFlow.dueledPlayerId }?.name!!}",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}