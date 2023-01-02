package com.szalik.ui.screens

import android.util.Log
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
import com.szalik.ui.common.KeepScreenOn
import com.szalik.ui.common.Timer
import com.szalik.ui.theme.SzalikTheme


@Composable
fun CardScreen() {
    KeepScreenOn()
    val dbRef = DatabaseConnection.getDatabase().getReference("lobbies")

    Log.i("CARD_SCREEN", "Recomposing...")

    SzalikTheme {
        if (GameFlow.isNight) {
            if (GameFlow.status == "IN_PROGRESS" && !GameFlow.awakenPlayersIds.contains(GameFlow.thisPlayerId)) {
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
                    if (GameFlow.thisPlayerId == GameFlow.currentPlayerId) {
                        if (GameFlow.showActionQuestion) {
                            //PYTANIE O CHĘĆ UŻYCIA AKCJI
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Masz jeszcze ${GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.actionsLeftCounter} akcje do wykorzystania." + "\n Czy chcesz z niej skorzystać?",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(Modifier.fillMaxWidth()) {
                                    Button(onClick = {
                                        GameFlow.showActionQuestion = false
                                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.BINOCULARS_EYE)
                                            GameFlow.showTotemLocation = true
                                        else
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
                            val text = handleRoleAction()

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
                                        var filteredList =
                                            GameFlow.listOfPlayers.filter { it.id != GameFlow.thisPlayerId }
                                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.BODYGUARD) {
                                            filteredList =
                                                filteredList.filter { it.id != GameFlow.lastProtectedPlayerId }
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
                                                        GameFlow.showChoiceList = false
                                                        dbRef
                                                            .child(GameFlow.getLobbyId())
                                                            .child("chosenPlayer")
                                                            .setValue(player.id)
                                                    })
                                        }
                                    }
                                } else if (GameFlow.showIdentity) {
                                    GameFlow.sharedIdentity?.let { player ->
                                        val fractionColor = when (player.card!!.role!!.fraction) {
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
                                    val player =
                                        GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }
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
                                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.CHIEF) {
                                            if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 2) {
                                                GameFlow.showChoiceList = true
                                            } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction != Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                                                dbRef.child(GameFlow.getLobbyId())
                                                    .child("currentPlayer")
                                                    .setValue("")
                                            } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                                                GameFlow.showChoiceList = true
                                            } else if (GameFlow.indiansKillCounter == 0) {
                                                dbRef.child(GameFlow.getLobbyId())
                                                    .child("currentPlayer")
                                                    .setValue("")
                                            } else {
                                                GameFlow.showChoiceList = true
                                            }
                                        } else {
                                            dbRef.child(GameFlow.getLobbyId())
                                                .child("currentPlayer")
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
                                Timer(
                                    totalTime = if (GameFlow.testMode) 5000 else 60000,
                                    mode = MeetingMode.ENTERTAINMENT
                                )
                            }
                            GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }
                                ?.let { player ->
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
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Dzień ${GameFlow.dayNumber}",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold
                )
                GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }
                    ?.let { player ->
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

fun handleRoleAction(): String {
    return when (GameFlow.listOfPlayers.find { it.id == GameFlow.currentPlayerId }?.card?.role) {
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
                GameFlow.showIdentity = true
                "Frakcja wyspowiadanej osoby to:"
            }
        }
        Role.EXECUTIONER -> {
            if (GameFlow.showChoiceList) {
                "Wybierz kogo chcesz zabić"
            } else {
                GameFlow.showConfirmButton = true
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
                GameFlow.showConfirmButton = true
                "Upiłeś wybraną osobę"
            }
        }
        Role.BODYGUARD -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę, którą będziesz chronić tej nocy"
            } else {
                GameFlow.showConfirmButton = true
                "Chronisz wybraną osobę"
            }
        }
        Role.WARLORD -> {
            if (GameFlow.showChoiceList) {
                if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS) {
                    "Naradź się z innymi Bandytami i wskaż kto ma przechować posążek tej nocy"
                } else {
                    "Naradź się z innymi bandytami i wskaż kogo przeszukać tej nocy"
                }
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Otrzymujesz posążek!"
                } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS) {
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
                GameFlow.showConfirmButton = true
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
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Udaje ci się wygrać posążek!"
                } else {
                    "Wybrana osoba nie miała przy sobie posążka"
                }
            }
        }
        Role.BLACKMAILER -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę którą chcesz zaszantażować"
            } else {
                GameFlow.showConfirmButton = true
                "Wybrana osoba będzie się ciebie słuchać"
            }
        }
        Role.CHIEF -> {
            if (GameFlow.showChoiceList) {
                if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 0) {
                    "Naradź się z innymi Indianinami i wskaż kto ma przechować tej nocy posążek"
                } else {
                    "Naradź się z innymi Indianinami i wskaż bladą twarz do zabicia"
                }
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 2) {
                    "Posążek przekazany"
                } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction != Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                    "Blada twarz została zabita"
                } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                    "Zabita blada twarz miała przy sobie posążek, przejmujesz go!"
                } else {
                    "Blada twarz została zabita"
                }
            }
        }
        Role.WARRIOR -> {
            if (GameFlow.showChoiceList) {
                "Wybierz bladą twarz do zabicia"
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Zabijasz bladą twarz i przejmujesz posążek!"
                } else {
                    "Wybrana osoba została zabita"
                }
            }
        }
        Role.BINOCULARS_EYE -> {
            "Osoba posiadająca posążek to:"
        }
        Role.SHAMANESS -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę którą chcesz otruć"
            } else {
                GameFlow.showConfirmButton = true
                "Wybrana osoba została otruta"
            }
        }
        Role.LONELY_COYOTE -> {
            if (GameFlow.showChoiceList) {
                "Wybierz bladą twarz do zabicia"
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Zabijasz bladą twarz i przejmujesz posążek!"
                } else {
                    "Wybrana osoba została zabita"
                }
            }
        }
        Role.BURNING_RAGE -> {
            if (GameFlow.showChoiceList) {
                "Wybierz bladą twarz do zabicia"
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Zabijasz bladą twarz i przejmujesz posążek!"
                } else {
                    "Wybrana osoba została zabita"
                }
            }
        }
        Role.SHAMAN -> {
            if (GameFlow.showChoiceList) {
                "Wybierz osobę której kartę chcesz poznać"
            } else {
                GameFlow.showIdentity = true
                "Tożsamość wybranej osoby to:"
            }
        }
        Role.GREAT_ALIEN -> {
            if (GameFlow.showChoiceList) {
                if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.ALIENS) {
                    "Naradź się z innymi Kosmitami i wskaż kto ma przechować tej nocy posążek"
                } else {
                    "Naradź się z innymi Kosmitami i wskaż ziemianina do przeszukania"
                }
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Udaje ci się zdobyć posążek!"
                } else {
                    "Wybrany ziemianin nie miał przy sobie posążka"
                }
            }
        }
        Role.PURPLE_SUCTION -> {
            if (GameFlow.showChoiceList) {
                "Wskaż ziemianina do przeszukania"
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Udaje ci się znaleźć posążek!"
                } else {
                    "Wybrany ziemianin nie miał przy sobie posążka"
                }
            }
        }
        Role.GREEN_TENTACLE -> {
            if (GameFlow.showChoiceList) {
                "Wskaż ziemianina do zabicia"
            } else {
                GameFlow.showConfirmButton = true
                if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                    "Zabijasz ziemianina i przejmujesz posążek!"
                } else {
                    "Wybrany ziemianin został zabity"
                }
            }
        }
        Role.MIND_EATER -> {
            if (GameFlow.showChoiceList) {
                "Wybierz  ziemianina którego tożsamość chcesz poznać"
            } else {
                GameFlow.showIdentity = true
                "Tożsamość wybranego ziemianina to"
            }
        }
        else -> {
            GameFlow.showChoiceList = false
            ""
        }
    }
}