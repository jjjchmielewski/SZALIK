package com.szalik.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szalik.logic.common.MeetingMode
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.Player
import com.szalik.logic.entertainment.enums.Fraction
import com.szalik.logic.entertainment.enums.Role
import com.szalik.ui.common.Timer
import com.szalik.ui.theme.SzalikTheme


@Composable
fun CardScreen() {
    val currentPlayerId by remember {
        mutableStateOf(GameFlow.currentPlayerId)
    }
    val awakenPlayersIds = remember {
        GameFlow.awakenPlayersIds
    }
    val players = remember {
        GameFlow.listOfPlayers
    }
    val dbRef = DatabaseConnection.getDatabase().getReference("lobbies")


    SzalikTheme {
        if (GameFlow.status == "IN_PROGRESS" && !awakenPlayersIds.contains(GameFlow.thisPlayerId)) {
            //GRACZ ŚPI
            Surface(
                modifier = Modifier.fillMaxSize(), color = Color(0xFF3F51B5)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Jest noc...",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A3A3A),
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }
        } else {
            //NOCNE AKCJE GRACZA
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
            ) {
                if (GameFlow.thisPlayerId == currentPlayerId) {
                    if (GameFlow.showActionQuestion) {
                        //PYTANIE O CHĘĆ UŻYCIA AKCJI
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Masz jeszcze ${players.find { it.id == GameFlow.thisPlayerId }?.card?.actionsLeftCounter} akcje do wykorzystania." + "\n Czy chcesz z niej skorzystać?",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(Modifier.fillMaxWidth()) {
                                Button(onClick = {
                                    GameFlow.showActionQuestion = false
                                    GameFlow.showChoiceList = true
                                }) {
                                    Text(text = "TAK")
                                }
                                Button(onClick = {
                                    GameFlow.showActionQuestion = false
                                    dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                        .setValue("")
                                }) {
                                    Text(text = "NIE")
                                }
                            }
                        }

                    } else {
                        val text = handleRoleAction(players)

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = text,
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (GameFlow.showChoiceList) {
                                LazyColumn {
                                    var filteredList = players.filter { it.id != GameFlow.thisPlayerId }
                                    if (players.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.BODYGUARD) {
                                        filteredList = filteredList.filter { it.id != GameFlow.lastProtectedPlayerId }
                                    }
                                    itemsIndexed(filteredList) { _, player ->
                                        Text(text = player.name,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Normal,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 10.dp)
                                                .clickable {
                                                    dbRef
                                                        .child(GameFlow.getLobbyId())
                                                        .child("chosenPlayer")
                                                        .setValue(player.id)
                                                    GameFlow.showChoiceList = false
                                                })
                                    }
                                }
                            } else if (GameFlow.showIdentity) {
                                GameFlow.sharedIdentity.let { player ->
                                    val fractionColor = when (player!!.card!!.role!!.fraction) {
                                        Fraction.CITY -> Color(0xFF8D7705)
                                        Fraction.BANDITS -> Color(0xFF6D6C6B)
                                        Fraction.INDIANS -> Color(0xFF680609)
                                        Fraction.ALIENS -> Color(0xFF045A01)
                                    }
                                    if (text == "Frakcja wyspowiadanej osoby to:") {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(fractionColor)
                                        ) {
                                            Text(
                                                text = player.card!!.role!!.fraction.polishName,
                                                textAlign = TextAlign.Center,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = player.card!!.role!!.polishName,
                                                textAlign = TextAlign.Center,
                                                fontSize = 30.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(fractionColor)
                                        ) {
                                            Text(
                                                text = "Frakcja: ${player.card!!.role!!.fraction.polishName}",
                                                textAlign = TextAlign.Center,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = player.card!!.role!!.description,
                                                textAlign = TextAlign.Center,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                    }
                                }

                                Button(onClick = {
                                    dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                        .setValue("")
                                    GameFlow.showIdentity = false
                                }) {
                                    Text(text = "Kontynuuj grę")
                                }
                            } else if (GameFlow.showTotemLocation) {
                                val player = players.find { it.id == GameFlow.playerWithTotemId }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = player!!.name,
                                        textAlign = TextAlign.Center,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Button(onClick = {
                                    dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                        .setValue("")
                                    GameFlow.showTotemLocation = false
                                }) {
                                    Text(text = "Kontynuuj grę")
                                }
                            } else if (GameFlow.showConfirmButton) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Text(
                                        text = text,
                                        textAlign = TextAlign.Center,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Button(onClick = {
                                    if (players.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.CHIEF) {
                                        if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 2) {
                                            GameFlow.showChoiceList = true
                                        } else if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction != Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                                            dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                                .setValue("")
                                        } else if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                                            GameFlow.showChoiceList = true
                                        } else if (GameFlow.indiansKillCounter == 0) {
                                            dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                                .setValue("")
                                        } else {
                                            GameFlow.showChoiceList = true
                                        }
                                    } else {
                                        dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                            .setValue("")
                                    }
                                    GameFlow.showConfirmButton = false
                                }) {
                                    Text(text = "Kontynuuj grę")
                                }
                            }
                        }
                    }
                } else {
                    //ODLICZANIE DO STARTU GRY
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (GameFlow.status == "STARTED") {
                            Timer(totalTime = 5000, mode = MeetingMode.ENTERTAINMENT)
                        }
                        players.find { it.id == GameFlow.thisPlayerId }?.let { player ->
                                val fractionColor = when (player.card!!.role!!.fraction) {
                                    Fraction.CITY -> Color(0xFF8D7705)
                                    Fraction.BANDITS -> Color(0xFF6D6C6B)
                                    Fraction.INDIANS -> Color(0xFF680609)
                                    Fraction.ALIENS -> Color(0xFF045A01)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = player.card!!.role!!.polishName,
                                        textAlign = TextAlign.Center,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(fractionColor)
                                ) {
                                    Text(
                                        text = "Frakcja: ${player.card!!.role!!.fraction.polishName}",
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = player.card!!.role!!.description,
                                        textAlign = TextAlign.Center,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }

                    }
                }
            }
        }
    }
}

fun handleRoleAction(players: List<Player>): String {
    return when (players.find { it.id == GameFlow.currentPlayerId }?.card?.role) {
        Role.COQUETTE -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę z którą chcesz się zapoznać"
            } else {
                GameFlow.showIdentity = true
                "Tożsamość poznanej osoby:"
            }
        }
        Role.SEDUCER -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę którą chcesz uwieść"
            } else {
                GameFlow.showConfirmButton = true
                "Wybrana osoba została uwiedziona"
            }
        }
        Role.SHERIFF -> {
            if (GameFlow.showChoiceList) {
                "Wybierz kogo chcesz zaaresztować"
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Udało ci się przejąć posążek!"
                } else {
                    "Zaaresztowana osoba nie miała posążka"
                }
            }
        }
        Role.PRIEST -> {
            if (GameFlow.showChoiceList) {
                "Wybierz kogo chcesz wyspowiadać"
            } else {
                GameFlow.showIdentity
                "Frakcja wyspowiadanej osoby to:"
            }
        }
        Role.EXECUTIONER -> {
            if (GameFlow.showChoiceList) {
                "Wybierz kogo chcesz zabić"
            } else {
                GameFlow.showConfirmButton
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Zabita osoba miała przy sobie posążek, teraz jest twój!"
                } else {
                    "Wybrana osoba została zabita"
                }
            }
        }
        Role.DRUNKARD -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę z którą pójdziesz się napić"
            } else {
                GameFlow.showConfirmButton
                "Upiłeś wybraną osobę"
            }
        }
        Role.BODYGUARD -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę, którą będziesz chronić tej nocy"
            } else {
                GameFlow.showConfirmButton
                "Chronisz wybraną osobę"
            }
        }
        Role.WARLORD -> {
            if (GameFlow.showChoiceList) {
                if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS) {
                    "Naradź się z innymi Bandytami i wskaż kto ma przechować posążek tej nocy"
                } else {
                    "Naradź się z innymi bandytami i wskaż kogo przeszukać tej nocy"
                }
            } else {
                GameFlow.showConfirmButton
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Otrzymujesz posążek!"
                } else if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS) {
                    "Posążek został przekazany"
                } else {
                    "Wybrana osoba nie miała przy sobie posążka"
                }
            }
        }
        Role.THIEF -> {
            if (GameFlow.showChoiceList) {
                "Kogo chcesz okraść?"
            } else {
                GameFlow.showConfirmButton
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Udaje ci się ukraść posążek!"
                } else {
                    "Wybrana osoba nie miała przy sobie posążka"
                }
            }
        }
        Role.GAMBLER -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę którą chcesz ograć w karty"
            } else {
                GameFlow.showConfirmButton
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Udaje ci się wygrać posążek!"
                } else {
                    "Wybrana osoba nie miała przy sobie posążka"
                }
            }
        }
        Role.CHIEF -> {
            if (GameFlow.showChoiceList) {
                if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 0) {
                    "Naradź się z innymi Indianinami i wskaż kto ma przechować tej nocy posążek"
                } else {
                    "Wskaż bladą twarz do zabicia"
                }
            } else {
                GameFlow.showConfirmButton
                if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 2) {
                    "Posążek przekazany"
                } else if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction != Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                    "Blada twarz została zabita"
                } else if (players.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                    "Zabita blada twarz miała przy sobie posążek, przejmujesz go!"
                } else {
                    "Blada twarz została zabita"
                }
            }
        }
        Role.WARRIOR -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.BINOCULARS_EYE -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.SHAMANESS -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.LONELY_COYOTE -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.BURNING_RAGE -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.SHAMAN -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.GREAT_ALIEN -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.PURPLE_SUCTION -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.GREEN_TENTACLE -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        Role.MIND_EATER -> {
            if (GameFlow.showChoiceList) {
                ""
            } else {
                ""
            }
        }
        else -> {
            GameFlow.showChoiceList = false
            ""
        }
    }
}