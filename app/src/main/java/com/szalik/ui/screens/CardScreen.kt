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
import androidx.compose.ui.unit.dp
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
                modifier = Modifier.fillMaxSize(), color = Color(0xFF812020)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wyeliminowany",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBBB9B9),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        } else if (GameFlow.isNight) {
            if (GameFlow.status == "IN_PROGRESS" && !GameFlow.awakenPlayersIds.contains(GameFlow.thisPlayerId)) {
                //GRACZ ŚPI
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color(0xFF32408F)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Jest noc...",
                            textAlign = TextAlign.Center,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFACABAB),
                            modifier = Modifier.fillMaxWidth()
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
                                modifier = Modifier.fillMaxSize().padding(20.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Masz jeszcze ${GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.actionsLeftCounter} akcje do wykorzystania." + "\n Czy chcesz z niej skorzystać?",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(Modifier.fillMaxWidth()) {
                                    Button(onClick = {
                                        GameFlow.showActionQuestion = false
                                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.BINOCULARS_EYE || GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }?.card?.role == Role.TAXMAN)
                                            GameFlow.showTotemLocation = true
                                        else
                                            GameFlow.showChoiceList = true
                                    }) {
                                        Text(
                                            text = "TAK",
                                            fontSize = 30.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colors.onPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Button(onClick = {
                                        GameFlow.showActionQuestion = false
                                        dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                            .setValue("")
                                    }) {
                                        Text(
                                            text = "NIE",
                                            fontSize = 30.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colors.onPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                        } else {
                            val text = RoleActionHandler.handle()

                            Column(
                                modifier = Modifier.fillMaxSize().padding(20.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = text,
                                        textAlign = TextAlign.Center,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
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
                                                    fontSize = 40.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.fillMaxWidth()
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
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(fractionColor)
                                            ) {
                                                Text(
                                                    text = "Frakcja: ${player.card!!.role!!.fraction.polishName}",
                                                    textAlign = TextAlign.Center,
                                                    fontSize = 30.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = player.card!!.role!!.description,
                                                    textAlign = TextAlign.Justify,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(onClick = {
                                        dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                            .setValue("")
                                        GameFlow.showIdentity = false
                                    }) {
                                        Text(
                                            text = "Kontynuuj grę",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colors.onPrimary,
                                            textAlign = TextAlign.Center
                                        )
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
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(onClick = {
                                        dbRef.child(GameFlow.getLobbyId()).child("currentPlayer")
                                            .setValue("")
                                        GameFlow.showTotemLocation = false
                                    }) {
                                        Text(
                                            text = "Kontynuuj grę",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colors.onPrimary,
                                            textAlign = TextAlign.Center
                                        )
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
                                        Text(
                                            text = "Kontynuuj grę",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colors.onPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        //ODLICZANIE DO STARTU GRY
                        Column(
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (GameFlow.status == "STARTED") {
                                Timer(
                                    totalTime = if (GameFlow.testMode) 5000 else 60000,
                                    mode = MeetingMode.ENTERTAINMENT
                                )
                                Spacer(modifier = Modifier.height(30.dp))
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
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (GameFlow.showDuelChoiceList) {
                        dbRef.child(GameFlow.getLobbyId()).child("dueledPlayer").setValue("")
                        dbRef.child(GameFlow.getLobbyId()).child("duelingPlayer").setValue("")
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Kogo chcesz wyzwać na pojedynek?",
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        ChoiceList(VotingMode.DUEL)

                    } else if (GameFlow.showSearchChoiceList) {
                        dbRef.child(GameFlow.getLobbyId()).child("playerToSearch").setValue("")
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Kogo chcesz zgłosić do przeszukania?",
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
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
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(30.dp))
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
                        Spacer(modifier = Modifier.height(20.dp))
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
                            Spacer(modifier = Modifier.height(20.dp))
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
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Rola: ${player.card!!.role!!.polishName}",
                                    textAlign = TextAlign.Center,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(fractionColor)
                            ) {
                                Text(
                                    text = "Frakcja: ${player.card!!.role!!.fraction.polishName}",
                                    textAlign = TextAlign.Center,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.height(40.dp))
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
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else if (GameFlow.winners != null) {
                        val text: String
                        val color: Color
                        when (GameFlow.winners) {
                            Fraction.CITY -> {
                                text = "Miastowi zdobyli posążek!"
                                color = Color(0xFF8D7705)
                            }
                            Fraction.BANDITS -> {
                                text = "Bandyci odpływają razem z posążkiem na aukcję antyków!"
                                color = Color(0xFF6D6C6B)
                            }
                            Fraction.INDIANS -> {
                                text = "Indianie zabili wszystkie blade twarze!"
                                color = Color(0xFF680609)
                            }
                            Fraction.ALIENS -> {
                                text = "Kosmici odlatują na swoją planetę!"
                                color = Color(0xFF045A01)
                            }
                            else -> {
                                text = ""
                                color = Color.Black
                            }
                        }
                        Surface(
                            color = color,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "KONIEC GRY",
                                        textAlign = TextAlign.Center,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(40.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = text,
                                        textAlign = TextAlign.Center,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    } else {
                        if ((GameFlow.listOfPlayers.size >= 16 && GameFlow.searchCounter == 3) || (GameFlow.listOfPlayers.size < 16 && GameFlow.searchCounter == 2)) {
                            dbRef.child(GameFlow.getLobbyId()).child("tts").setValue("Zapada noc, wszyscy idą spać.")
                            GameFlow.night()
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Dzień ${GameFlow.dayNumber}",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        if (showCard) {
                            GameFlow.listOfPlayers.find { it.id == GameFlow.thisPlayerId }
                                ?.let { player ->
                                    RoleView(player = player)
                                }
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                        Button(onClick = {
                            showCard = !showCard
                        }) {
                            Text(
                                text = if (showCard) "Ukryj szczegóły karty" else "Pokaż szczegóły karty",
                                textAlign = TextAlign.Center,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(onClick = {
                            GameFlow.showDuelChoiceList = true
                        }) {
                            Text(
                                text = "Pojedynek",
                                textAlign = TextAlign.Center,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = {
                            GameFlow.showSearchChoiceList = true
                        }) {
                            Text(
                                text = "Przeszukanie",
                                textAlign = TextAlign.Center,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
