package com.szalik.ui.screens

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.szalik.logic.entertainment.enums.MeetingMode
import com.szalik.logic.common.RoleActionHandler
import com.szalik.logic.common.TTSEngine
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.enums.Fraction
import com.szalik.logic.entertainment.enums.Role
import com.szalik.logic.entertainment.enums.VotingMode
import com.szalik.ui.common.*
import com.szalik.ui.theme.SzalikTheme


@Composable
fun CardScreen() {
    KeepScreenOn()
    val context = LocalContext.current
    val dbRef = DatabaseConnection.getDatabase().getReference("lobbies")

    var init by remember {
        mutableStateOf(false)
    }

    var showCard by remember {
        mutableStateOf(false)
    }

    Log.i("CARD_SCREEN", "Recomposing...")
    if (!init) {
        TTSEngine.getTTS(context)
        init = true
    }

    if (GameFlow.ttsMessage != null) {
        TTSEngine.getTTS(context).speak(GameFlow.ttsMessage, TextToSpeech.QUEUE_FLUSH, null, "")
        GameFlow.ttsMessage = null
    }

    SzalikTheme {
        if (GameFlow.listOfPlayers.none { it.id == GameFlow.thisPlayerId }) {
            Surface(
                modifier = Modifier.fillMaxSize(), color = Color(0xFF5A5A5A)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wyeliminowany",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A3A3A),
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }
        } else if (GameFlow.isNight) {
            if (GameFlow.status == "IN_PROGRESS" && !GameFlow.awakenPlayersIds.contains(GameFlow.thisPlayerId)) {
                //GRACZ ŚPI
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color(0xFF3F51B5)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
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
                                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.BINOCULARS_EYE || GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.TAXMAN)
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
                            val text = RoleActionHandler.handle()

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
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                if (GameFlow.showChoiceList) {
                                    if (text.contains("Naradź się z innymi Kosmitami i wskaż kto ma przechować tej nocy posążek")) {
                                        ChoiceList(fraction = Fraction.ALIENS)
                                    } else if (text.contains("Naradź się z innymi Indianinami i wskaż kto ma przechować tej nocy posążek")) {
                                        ChoiceList(fraction = Fraction.INDIANS)
                                    } else if (text.contains("Naradź się z innymi Bandytami i wskaż kto ma przechować posążek tej nocy")) {
                                        ChoiceList(fraction = Fraction.BANDITS)
                                    } else {
                                        ChoiceList()
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
                                    Button(onClick = {
                                        GameFlow.showConfirmButton = false
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
                                    RoleView(player = player)
                                }
                        }
                    }
                }
            }
        } else {
            //WIDOK DNIA
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (GameFlow.showDuelChoiceList) {
                        dbRef.child(GameFlow.getLobbyId()).child("dueledPlayer").setValue("")
                        dbRef.child(GameFlow.getLobbyId()).child("duelingPlayer").setValue("")
                        Text(
                            text = "Kogo chcesz wyzwać na pojedynek?",
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        ChoiceList(VotingMode.DUEL)

                    } else if (GameFlow.showSearchChoiceList) {
                        dbRef.child(GameFlow.getLobbyId()).child("playerToSearch").setValue("")
                        Text(
                            text = "Kogo chcesz zgłosić do przeszukania?",
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        ChoiceList(VotingMode.SEARCH)

                    } else if (GameFlow.showVoting) {
                        if (GameFlow.duelingPlayerId != "")
                            Voting(mode = VotingMode.DUEL)
                        else if (GameFlow.playerToSearchId != "" && !GameFlow.searched)
                            Voting(mode = VotingMode.SEARCH)
                        else
                            Voting(mode = VotingMode.HANG)

                    } else if (GameFlow.showConfirmButton) {
                        val text: String
                        val function: () -> Unit
                        if (GameFlow.duelingPlayerId != "") {
                            val goodGunslingerId = GameFlow.listOfPlayers.find { it.card?.role == Role.GOOD_GUNSLINGER }?.id
                            val badGunslingerId = GameFlow.listOfPlayers.find { it.card?.role == Role.BAD_GUNSLINGER }?.id

                            if (GameFlow.votingResult!! > 100) {
                                text =
                                    "Pojedynek zwyciężył ${GameFlow.listOfPlayers.find { it.id == GameFlow.duelingPlayerId }?.name} dzięki decyzji sędziego ${GameFlow.listOfPlayers.find { it.card?.role == Role.JUDGE }?.name}"
                                function = {
                                    GameFlow.eliminateDuringDay(GameFlow.dueledPlayerId)
                                    GameFlow.showConfirmButton = false
                                    GameFlow.duelingPlayerId = ""
                                    GameFlow.dueledPlayerId = ""
                                }
                            } else if (GameFlow.votingResult!! < -100) {
                                text =
                                    "Pojedynek zwyciężył ${GameFlow.listOfPlayers.find { it.id == GameFlow.dueledPlayerId }?.name} dzięki decyzji sędziego ${GameFlow.listOfPlayers.find { it.card?.role == Role.JUDGE }?.name}"
                                function = {
                                    GameFlow.eliminateDuringDay(GameFlow.duelingPlayerId)
                                    GameFlow.showConfirmButton = false
                                    GameFlow.duelingPlayerId = ""
                                    GameFlow.dueledPlayerId = ""
                                }
                            } else if ((GameFlow.duelingPlayerId == goodGunslingerId && GameFlow.dueledPlayerId != badGunslingerId) || (GameFlow.dueledPlayerId == goodGunslingerId && GameFlow.duelingPlayerId != badGunslingerId)) {
                                text = "Pojedynek zwyciężył ${GameFlow.listOfPlayers.find { it.id == GameFlow.duelingPlayerId }?.name}"
                                function = {
                                    GameFlow.eliminateDuringDay(GameFlow.dueledPlayerId)
                                    GameFlow.showConfirmButton = false
                                    GameFlow.duelingPlayerId = ""
                                    GameFlow.dueledPlayerId = ""
                                }
                            } else if ((GameFlow.duelingPlayerId != goodGunslingerId && GameFlow.dueledPlayerId == badGunslingerId) || (GameFlow.dueledPlayerId != goodGunslingerId && GameFlow.duelingPlayerId == badGunslingerId)) {
                                text = "Pojedynek zwyciężył ${GameFlow.listOfPlayers.find { it.id == GameFlow.dueledPlayerId }?.name}"
                                function = {
                                    GameFlow.eliminateDuringDay(GameFlow.duelingPlayerId)
                                    GameFlow.showConfirmButton = false
                                    GameFlow.duelingPlayerId = ""
                                    GameFlow.dueledPlayerId = ""
                                }
                            } else if (GameFlow.votingResult!! > 0) {
                                text = "Pojedynek zwyciężył ${GameFlow.listOfPlayers.find { it.id == GameFlow.duelingPlayerId }?.name}"
                                function = {
                                    GameFlow.eliminateDuringDay(GameFlow.dueledPlayerId)
                                    GameFlow.showConfirmButton = false
                                    GameFlow.duelingPlayerId = ""
                                    GameFlow.dueledPlayerId = ""
                                }
                            } else if (GameFlow.votingResult!! < 0) {
                                text = "Pojedynek zwyciężył ${GameFlow.listOfPlayers.find { it.id == GameFlow.dueledPlayerId }?.name}"
                                function = {
                                    GameFlow.eliminateDuringDay(GameFlow.duelingPlayerId)
                                    GameFlow.showConfirmButton = false
                                    GameFlow.duelingPlayerId = ""
                                    GameFlow.dueledPlayerId = ""
                                }
                            } else {
                                text = "Remis - obydwaj gracze zostają wyeliminowani"
                                function = {
                                    GameFlow.eliminateDuringDay(GameFlow.dueledPlayerId)
                                    GameFlow.eliminateDuringDay(GameFlow.duelingPlayerId)
                                    GameFlow.showConfirmButton = false
                                    GameFlow.duelingPlayerId = ""
                                    GameFlow.dueledPlayerId = ""
                                }
                            }

                        } else if (GameFlow.playerToSearchId != "" && !GameFlow.searched) {
                            if (GameFlow.votingResult!! > 0) {
                                if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerToSearchId }?.card?.hasTotem == true) {
                                    text =
                                        "Przy graczu ${GameFlow.listOfPlayers.find { it.id == GameFlow.playerToSearchId }?.name} znaleziono posążek!"
                                    function = {
                                        GameFlow.winners = Fraction.CITY
                                        GameFlow.showConfirmButton = false
                                        GameFlow.searchCounter++
                                    }
                                } else {
                                    text =
                                        "Gracz ${GameFlow.listOfPlayers.find { it.id == GameFlow.playerToSearchId }?.name} został przeszukany - nie miał posążka"
                                    function = {
                                        GameFlow.searched = true
                                        GameFlow.showConfirmButton = false
                                        GameFlow.showVoting = true
                                        GameFlow.searchCounter++
                                    }
                                }
                            } else {
                                text =
                                    "Gracz ${GameFlow.listOfPlayers.find { it.id == GameFlow.playerToSearchId }?.name} nie został przeszukany"
                                function = {
                                    GameFlow.showConfirmButton = false
                                    GameFlow.playerToSearchId = ""
                                }
                            }

                        } else if (GameFlow.playerToSearchId != "" && GameFlow.searched) {
                            if (GameFlow.votingResult!! > 0) {
                                text = "Gracz ${GameFlow.listOfPlayers.find { it.id == GameFlow.playerToSearchId }?.name} został powieszony"
                                function = {
                                    GameFlow.eliminateDuringDay(GameFlow.playerToSearchId)
                                    GameFlow.playerToSearchId = ""
                                    GameFlow.searched = false
                                    GameFlow.showConfirmButton = false
                                }
                            } else {
                                text =
                                    "Gracz ${GameFlow.listOfPlayers.find { it.id == GameFlow.playerToSearchId }?.name} został ułaskawiony"
                                function = {
                                    GameFlow.playerToSearchId = ""
                                    GameFlow.searched = false
                                    GameFlow.showConfirmButton = false
                                }
                            }
                        } else {
                            text = ""
                            function = {}
                        }
                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(onClick = {
                            function.invoke()
                            dbRef.child(GameFlow.getLobbyId()).child("voted").setValue(0)
                            dbRef.child(GameFlow.getLobbyId()).child("voting").setValue(0)
                        }) {
                            Text(
                                text = "Kontynuuj",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    } else if (GameFlow.showEliminated && GameFlow.eliminatedPlayers.isNotEmpty()) {
                        Text(
                            text = "Wyeliminowano gracza:",
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        GameFlow.eliminatedPlayers.first().let { playerId ->
                            val player = GameFlow.listOfPlayers.find { it.id == playerId }

                            val fractionColor = when (player?.card!!.role!!.fraction) {
                                Fraction.CITY -> Color(0xFF8D7705)
                                Fraction.BANDITS -> Color(0xFF6D6C6B)
                                Fraction.INDIANS -> Color(0xFF680609)
                                Fraction.ALIENS -> Color(0xFF045A01)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = player.name,
                                    textAlign = TextAlign.Center,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Rola: ${player.card!!.role!!.polishName}",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth()
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
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Button(onClick = {
                                GameFlow.listOfPlayers.remove(GameFlow.listOfPlayers.find { it.id == GameFlow.eliminatedPlayers.first() })
                                GameFlow.eliminatedPlayers.removeFirst()
                                if (GameFlow.eliminatedPlayers.isEmpty()) {
                                    GameFlow.showEliminated = false
                                }
                            }) {
                                Text(
                                    text = "Kontynuuj",
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else if (GameFlow.winners != null) {
                        val text = when (GameFlow.winners) {
                            Fraction.CITY -> {
                                "Miastowi zdobyli posążek!"
                            }
                            Fraction.BANDITS -> {
                                "Bandyci odpływają razem z posążkiem na aukcję antyków!"
                            }
                            Fraction.INDIANS -> {
                                "Indianie zabili wszystkie blade twarze!"
                            }
                            Fraction.ALIENS -> {
                                "Kosmici odlatują na swoją planetę!"
                            }
                            else -> {
                                ""
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "KONIEC GRY",
                                textAlign = TextAlign.Center,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = text,
                                textAlign = TextAlign.Center,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        if ((GameFlow.listOfPlayers.size >= 16 && GameFlow.searchCounter == 3) || (GameFlow.listOfPlayers.size < 16 && GameFlow.searchCounter == 2)) {
                            dbRef.child(GameFlow.getLobbyId()).child("tts").setValue("Zapada noc, wszyscy idą spać.")
                            GameFlow.night()
                        }
                        Text(
                            text = "Dzień ${GameFlow.dayNumber}",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (showCard) {
                            GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }
                                ?.let { player ->
                                    RoleView(player = player)
                                }
                        }
                        Button(onClick = {
                            showCard = !showCard
                        }) {
                            Text(
                                text = if (showCard) "Ukryj szczegóły karty" else "Pokaż szczegóły karty",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                GameFlow.showDuelChoiceList = true
                            }) {
                                Text(
                                    text = "Pojedynek",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Button(onClick = {
                                GameFlow.showSearchChoiceList = true
                            }) {
                                Text(
                                    text = "Przeszukanie",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
