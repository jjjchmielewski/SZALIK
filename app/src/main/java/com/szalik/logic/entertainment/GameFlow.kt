package com.szalik.logic.entertainment

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.cards.GameCard
import com.szalik.logic.entertainment.enums.Fraction
import com.szalik.logic.entertainment.enums.FractionsDistribution
import com.szalik.logic.entertainment.enums.Role

class GameFlow {
    companion object {
        var testMode: Boolean = false
        var isHost: Boolean = false
        var isNight: Boolean by mutableStateOf(true)
        var actionTakeover: Role? by mutableStateOf(null)
        val listOfPlayers = mutableStateListOf<Player>()
        val awakenPlayersIds = mutableStateListOf<String>()
        var thisPlayerId: String? = null
        var playerInGame = false
        var winners by mutableStateOf<Fraction?>(null)

        var status by mutableStateOf("")
        var currentPlayerId by mutableStateOf("")
        var chosenPlayerId: String = ""
        var duelingPlayerId by mutableStateOf("")
        var dueledPlayerId by mutableStateOf("")
        var votingResult by mutableStateOf<Long?>(null)
        var playerToSearchId by mutableStateOf("")
        var searched by mutableStateOf(false)
        var searchCounter by mutableStateOf(0)
        var aliensSignalCounter = 0

        var showActionQuestion by mutableStateOf(false)
        var showChoiceList by mutableStateOf(true)
        var showIdentity by mutableStateOf(false)
        var showTotemLocation by mutableStateOf(false)
        var showConfirmButton by mutableStateOf(false)
        var showDuelChoiceList by mutableStateOf(false)
        var showSearchChoiceList by mutableStateOf(false)
        var showVoting by mutableStateOf(false)
        var showEliminated by mutableStateOf(false)

        var dayNumber = 0
        val eliminatedPlayers = mutableListOf<String>()
        var sharedIdentity by mutableStateOf<Player?>(null)
        var playerWithTotemId = ""
        var indiansKillCounter = 9
        var lastProtectedPlayerId = ""
        var indiansTotemTakeover = false
        private val rolesQueue = mutableListOf<Role>()
        private var lobbyId: String? = null
        private var dbRef = DatabaseConnection.getDatabase().getReference("lobbies")


        fun prepareGameByHost() {
            giveCards(listOfPlayers.size)
            dbRef.child(lobbyId!!).child("currentPlayer").setValue("")
            dbRef.child(lobbyId!!).child("chosenPlayer").setValue("")
            dbRef.child(lobbyId!!).child("state").setValue("STARTED")
            dbRef.child(lobbyId!!).child("voted").setValue(0)
            dbRef.child(lobbyId!!).child("voting").setValue(0)
            dbRef.child(lobbyId!!).child("playerToSearch").setValue("")
            dbRef.child(lobbyId!!).child("dueledPlayer").setValue("")
            dbRef.child(lobbyId!!).child("duelingPlayer").setValue("")
            checkVotesGiven()
            checkPlayerToSearch()
            checkDueledPlayer()
            checkDuelingPlayer()
            checkCurrentPlayer()
            checkChosenPlayer()
            nightZero()
            for (player in listOfPlayers) {
                Log.i("GAME_FLOW", player.card?.role?.polishName!!)
            }
        }

        fun prepareGameByGuest() {
            checkVotesGiven()
            checkPlayerToSearch()
            checkDueledPlayer()
            checkDuelingPlayer()
            checkCurrentPlayer()
            checkChosenPlayer()
            nightZero()
        }

        fun startGame() {
            Log.i("GAME_FLOW", "Starting game")
            if (isHost) dbRef.child(lobbyId!!).child("state").setValue("IN_PROGRESS")
        }

        fun setLobbyId(lobbyId: String): Boolean {
            this.lobbyId = lobbyId
            checkStatus()
            getPlayers()

            return true
        }

        fun getLobbyId(): String {
            return lobbyId!!
        }

        private fun nightZero() {
            listOfPlayers.find { it.card?.role == Role.WARLORD }?.card?.hasTotem = true
            whereIsTotem()

            rolesQueue.add(Role.COQUETTE)
            rolesQueue.add(Role.SEDUCER)
            rolesQueue.add(Role.SHERIFF)
            rolesQueue.add(Role.PRIEST)
            rolesQueue.add(Role.WARLORD)
            rolesQueue.add(Role.BLACKMAILER)
        }

        fun night() {
            rolesQueue.add(Role.SHERIFF)
            rolesQueue.add(Role.PRIEST)
            rolesQueue.add(Role.TAXMAN)
            rolesQueue.add(Role.BODYGUARD)
            rolesQueue.add(Role.DRUNKARD)
            rolesQueue.add(Role.EXECUTIONER)
            rolesQueue.add(Role.WARLORD)
            rolesQueue.add(Role.GAMBLER)
            rolesQueue.add(Role.THIEF)
            rolesQueue.add(Role.SHAMAN)
            rolesQueue.add(Role.CHIEF)
            rolesQueue.add(Role.LONELY_COYOTE)
            rolesQueue.add(Role.BURNING_RAGE)
            rolesQueue.add(Role.WARRIOR)
            rolesQueue.add(Role.BINOCULARS_EYE)
            rolesQueue.add(Role.MIND_EATER)
            rolesQueue.add(Role.GREAT_ALIEN)
            rolesQueue.add(Role.GREEN_TENTACLE)
            rolesQueue.add(Role.PURPLE_SUCTION)
            isNight = true
            searchCounter = 0
            handleNextPlayer()
        }

        private fun day() {
            showConfirmButton = false
            indiansKillCounter = 0
            indiansTotemTakeover = false
            sharedIdentity = null
            listOfPlayers.forEach {
                if (it.card!!.isJailed) it.card!!.isJailed = false
                if (it.card!!.isDrunk) it.card!!.isDrunk = false
                if (it.card!!.isProtected) it.card!!.isProtected = false
                if (it.card!!.isPlaying) it.card!!.isPlaying = false
            }
            showEliminated = true
            if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.CITY) {
                winners = Fraction.CITY
            }
            if (aliensSignalCounter == 3) {
                winners = Fraction.ALIENS
            }
            if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS &&
                ((dayNumber >= 4 && listOfPlayers.size <= 16) || dayNumber >= 5)
            ) {
                winners = Fraction.BANDITS
            }
            if (listOfPlayers.none { it.card?.role?.fraction != Fraction.INDIANS } && listOfPlayers.find { it.card?.role?.fraction == Fraction.INDIANS } != null) {
                winners = Fraction.INDIANS
            }
        }


        private fun handleNextPlayer() {
            Log.i("GAME_FLOW", "Role queue:")
            rolesQueue.forEach {
                Log.i("ROLES_QUEUE", it.name)
            }
            showActionQuestion = false
            actionTakeover = null
            if (rolesQueue.isNotEmpty()) {
                var nextPlayer = listOfPlayers.find { it.card?.role == rolesQueue.first() }
                if (nextPlayer != null) {
                    if (nextPlayer.card?.isDrunk == true || nextPlayer.card?.isJailed == true || nextPlayer.card?.isPlaying == true) {
                        when (nextPlayer.card?.role) {
                            Role.WARLORD -> {
                                listOfPlayers.find { it.id != nextPlayer?.id && it.card?.role?.fraction == Fraction.BANDITS && !(it.card?.isDrunk == true || it.card?.isJailed == true || it.card?.isPlaying == true) }
                                    ?.let {
                                        nextPlayer = it
                                        actionTakeover = Role.WARLORD
                                    } ?: run {
                                    rolesQueue.removeFirst()
                                    handleNextPlayer()
                                    return
                                }
                            }
                            Role.CHIEF -> {
                                listOfPlayers.find { it.id != nextPlayer?.id && it.card?.role?.fraction == Fraction.INDIANS && !(it.card?.isDrunk == true || it.card?.isJailed == true || it.card?.isPlaying == true) }
                                    ?.let {
                                        nextPlayer = it
                                        actionTakeover = Role.CHIEF
                                    } ?: run {
                                    rolesQueue.removeFirst()
                                    handleNextPlayer()
                                    return
                                }
                            }
                            Role.GREAT_ALIEN -> {
                                listOfPlayers.find { it.id != nextPlayer?.id && it.card?.role?.fraction == Fraction.ALIENS && !(it.card?.isDrunk == true || it.card?.isJailed == true || it.card?.isPlaying == true) }
                                    ?.let {
                                        nextPlayer = it
                                        actionTakeover = Role.GREAT_ALIEN
                                    } ?: run {
                                    rolesQueue.removeFirst()
                                    handleNextPlayer()
                                    return
                                }
                            }
                            else -> {
                                rolesQueue.removeFirst()
                                handleNextPlayer()
                                return
                            }
                        }
                    } else {
                        if (nextPlayer?.card?.role == Role.LONELY_COYOTE && listOfPlayers.filter { it.card?.role?.fraction == Fraction.INDIANS }.size > 1) {
                            rolesQueue.removeFirst()
                            handleNextPlayer()
                        }

                        if (nextPlayer?.card?.role == Role.BURNING_RAGE && !indiansTotemTakeover) {
                            rolesQueue.removeFirst()
                            handleNextPlayer()
                        }

                        when (nextPlayer?.card?.actionsLeftCounter) {
                            999 -> {
                                showChoiceList = true
                            }
                            0 -> {
                                rolesQueue.removeFirst()
                                handleNextPlayer()
                                return
                            }
                            else -> {
                                showActionQuestion = true
                                showChoiceList = false
                            }
                        }
                    }

                    when (nextPlayer?.card?.role?.fraction) {
                        Fraction.BANDITS -> {
                            if (nextPlayer?.card?.role != Role.BLACKMAILER && actionTakeover == null) {
                                listOfPlayers.filter { it.card?.role?.fraction == Fraction.BANDITS && it.card?.isJailed == false && it.card?.isDrunk == false && it.card?.isPlaying == false }
                                    .let {
                                        it.forEach { player ->
                                            awakenPlayersIds.add(player.id)
                                        }
                                    }
                            } else {
                                awakenPlayersIds.add(nextPlayer!!.id)
                            }
                        }

                        Fraction.INDIANS -> {
                            listOfPlayers.filter { it.card?.role?.fraction == Fraction.INDIANS && it.card?.isJailed == false && it.card?.isDrunk == false && it.card?.isPlaying == false }
                                .let {
                                    it.forEach { player ->
                                        awakenPlayersIds.add(player.id)
                                    }
                                }
                        }

                        Fraction.ALIENS -> {
                            listOfPlayers.filter { it.card?.role?.fraction == Fraction.ALIENS && it.card?.isJailed == false && it.card?.isDrunk == false && it.card?.isPlaying == false }
                                .let {
                                    it.forEach { player ->
                                        awakenPlayersIds.add(player.id)
                                    }
                                }
                        }

                        else -> awakenPlayersIds.add(nextPlayer!!.id)
                    }
                    rolesQueue.removeFirst()
                    dbRef.child(lobbyId!!).child("currentPlayer").setValue(nextPlayer!!.id)
                } else {
                    rolesQueue.removeFirst()
                    handleNextPlayer()
                }
            } else {
                dayNumber++
                Log.i("GAME_FLOW", "Morning no. $dayNumber")
                isNight = false
                day()
            }
        }

        private fun handleChoice() {
            Log.i(
                "GAME_FLOW", "Handling choice for chosen: ${listOfPlayers.find { it.id == chosenPlayerId }?.name}"
            )
            var voiceMessage = ""
            when (if (actionTakeover != null) actionTakeover else listOfPlayers.find { it.id == currentPlayerId }?.card?.role) {
                Role.COQUETTE -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                    awakenPlayersIds.add(chosenPlayerId)
                }
                Role.SEDUCER -> {
                    listOfPlayers.find { it.id == chosenPlayerId }?.card?.isSeduced = true
                    awakenPlayersIds.add(chosenPlayerId)
                }
                Role.SHERIFF -> {
                    listOfPlayers.find { it.id == chosenPlayerId }.let {
                        it?.card?.isJailed = true
                        if (it?.card?.hasTotem == true) {
                            passTotemTo(Role.SHERIFF)
                        }
                    }
                }
                Role.PRIEST -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                }
                Role.EXECUTIONER -> {
                    eliminate(currentPlayerId, chosenPlayerId)
                }
                Role.DRUNKARD -> {
                    listOfPlayers.find { it.id == chosenPlayerId }?.card?.isDrunk = true
                }
                Role.BODYGUARD -> {
                    listOfPlayers.find { it.id == chosenPlayerId }?.card?.isProtected = true
                    lastProtectedPlayerId = chosenPlayerId
                }
                Role.TAXMAN -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                }
                Role.WARLORD -> {
                    if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS) {
                        passTotemTo(chosenPlayerId)
                    } else {
                        if (listOfPlayers.find { it.id == chosenPlayerId }?.card?.hasTotem == true) {
                            passTotemTo(currentPlayerId)
                        }
                    }
                }
                Role.THIEF -> {
                    if (listOfPlayers.find { it.id == chosenPlayerId }?.card?.hasTotem == true) {
                        passTotemTo(Role.THIEF)
                    }
                }
                Role.GAMBLER -> {
                    listOfPlayers.find { it.id == chosenPlayerId }?.card?.isPlaying = true
                    if (listOfPlayers.find { it.id == chosenPlayerId }?.card?.hasTotem == true) {
                        passTotemTo(Role.GAMBLER)
                    }
                }
                Role.BLACKMAILER -> {
                    listOfPlayers.find { it.id == chosenPlayerId }?.card?.isBlackmailed = true
                    awakenPlayersIds.add(chosenPlayerId)
                }
                Role.CHIEF -> {
                    if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && indiansKillCounter == 0) {
                        indiansKillCounter = 2
                        passTotemTo(chosenPlayerId)
                    } else if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction != Fraction.INDIANS && indiansKillCounter == 0) {
                        indiansKillCounter = 1
                        eliminate(currentPlayerId, chosenPlayerId)
                    } else if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && indiansKillCounter == 1) {
                        eliminate(currentPlayerId, chosenPlayerId)
                        indiansKillCounter--
                    } else {
                        eliminate(currentPlayerId, chosenPlayerId)
                        indiansKillCounter--
                    }
                }
                Role.WARRIOR -> {
                    eliminate(currentPlayerId, chosenPlayerId)
                }
                Role.BINOCULARS_EYE -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                }
                Role.LONELY_COYOTE -> {
                    eliminate(currentPlayerId, chosenPlayerId)
                }
                Role.BURNING_RAGE -> {
                    eliminate(currentPlayerId, chosenPlayerId)
                }
                Role.SHAMAN -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                }
                Role.GREAT_ALIEN -> {
                    if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.ALIENS) {
                        aliensSignalCounter++
                        passTotemTo(chosenPlayerId)
                    } else {
                        if (listOfPlayers.find { it.id == chosenPlayerId }?.card?.hasTotem == true) {
                            aliensSignalCounter++
                            passTotemTo(currentPlayerId)
                        }
                    }
                }
                Role.PURPLE_SUCTION -> {
                    if (listOfPlayers.find { it.id == chosenPlayerId }?.card?.hasTotem == true) {
                        passTotemTo(Role.PURPLE_SUCTION)
                    }
                }
                Role.GREEN_TENTACLE -> {
                    eliminate(currentPlayerId, chosenPlayerId)
                }
                Role.MIND_EATER -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                }
                else -> {}
            }
            chosenPlayerId = ""
            dbRef.child(lobbyId!!).child("chosenPlayer").setValue("")
        }

        private fun whereIsTotem() {
            listOfPlayers.forEach {
                if (it.card!!.hasTotem) {
                    playerWithTotemId = it.id
                    return@forEach
                }
            }
        }

        private fun passTotemTo(role: Role) {
            whereIsTotem()
            listOfPlayers.find { it.id == playerWithTotemId }?.card?.hasTotem = false
            listOfPlayers.find { it.card?.role == role }?.card?.hasTotem = true
            playerWithTotemId = listOfPlayers.find { it.card?.role == role }?.id!!
            if (listOfPlayers.find { it.card?.role == role }?.card?.role?.fraction == Fraction.INDIANS) indiansTotemTakeover = true
        }

        private fun passTotemTo(id: String) {
            whereIsTotem()
            listOfPlayers.find { it.id == playerWithTotemId }?.card?.hasTotem = false
            listOfPlayers.find { it.id == id }?.card?.hasTotem = true
            playerWithTotemId = id
            if (listOfPlayers.find { it.id == id }?.card?.role?.fraction == Fraction.INDIANS) indiansTotemTakeover = true
        }

        private fun giveCards(numberOfPlayers: Int) {
            val distribution = FractionsDistribution.fromPLayersNumber(numberOfPlayers)
            val allRoles = Role.values().toMutableList()
            val rolesToGive = mutableListOf<Role>()

            if (testMode) {
                rolesToGive.add(Role.COQUETTE)
                rolesToGive.add(Role.SEDUCER)
                rolesToGive.add(Role.SHERIFF)
                rolesToGive.add(Role.PRIEST)
                rolesToGive.add(Role.WARLORD)
                rolesToGive.add(Role.BLACKMAILER)
            } else {
                rolesToGive.add(Role.SHERIFF)
                allRoles.remove(Role.SHERIFF)
                rolesToGive.add(Role.WARLORD)
                allRoles.remove(Role.WARLORD)
                rolesToGive.add(Role.CHIEF)
                allRoles.remove(Role.CHIEF)
                for (i in distribution.city downTo 2) {
                    val role = allRoles.filter { it.fraction == Fraction.CITY }.random()
                    allRoles.remove(role)
                    rolesToGive.add(role)
                }
                for (i in distribution.bandits downTo 2) {
                    val role = allRoles.filter { it.fraction == Fraction.BANDITS }.random()
                    allRoles.remove(role)
                    rolesToGive.add(role)
                }
                for (i in distribution.indians downTo 2) {
                    val role = allRoles.filter { it.fraction == Fraction.INDIANS }.random()
                    allRoles.remove(role)
                    rolesToGive.add(role)
                }
                for (i in distribution.aliens downTo 1) {
                    val role = allRoles.filter { it.fraction == Fraction.ALIENS }.random()
                    allRoles.remove(role)
                    rolesToGive.add(role)
                }
            }


            for (gamer in listOfPlayers) {
                if (testMode) {
                    gamer.card = GameCard(rolesToGive.first())
                    rolesToGive.removeFirst()
                } else {
                    val role = rolesToGive.random()
                    gamer.card = GameCard(role)
                    rolesToGive.remove(role)
                }
                dbRef.child(lobbyId!!).child("players").child(gamer.id).setValue(gamer)
            }
        }

        private fun eliminate(predatorId: String, preyId: String) {
            val chosenPlayer = listOfPlayers.find { it.id == preyId }
            var voiceMessage = "Wybrana osoba nie została zabita ponieważ "
            if (chosenPlayer!!.card!!.isJailed) voiceMessage += "jest w areszcie"
            else if (chosenPlayer.card!!.isProtected) voiceMessage += "jest chroniona"
            else {
                eliminatedPlayers.add(preyId)
                listOfPlayers.find { it.id == preyId }?.eliminated = true
                passTotemTo(predatorId)
                removeBlackmailerSeducerEffect(chosenPlayer.card?.role!!)
                showEliminated = true
                voiceMessage = "Wybrana osoba została zabita"
            }
        }

        fun eliminateDuringDay(preyId: String) {
            val chosenPlayer = listOfPlayers.find { it.id == preyId }
            if (chosenPlayer?.card?.hasTotem == true) {
                eliminatedPlayers.add(preyId)
                listOfPlayers.find { it.id == preyId }?.eliminated = true
                showEliminated = true
                winners = Fraction.CITY
            } else {
                eliminatedPlayers.add(preyId)
                listOfPlayers.find { it.id == preyId }?.eliminated = true
                showEliminated = true
                removeBlackmailerSeducerEffect(chosenPlayer?.card?.role!!)
            }
        }

        private fun removeBlackmailerSeducerEffect(role: Role) {
            if (role == Role.SEDUCER) {
                listOfPlayers.forEach {
                    if (it.card!!.isJailed) it.card!!.isSeduced = false
                }
            } else if (role == Role.BLACKMAILER) {
                listOfPlayers.forEach {
                    if (it.card!!.isJailed) it.card!!.isBlackmailed = false
                }
            }
        }

        private fun getPlayers() {
            dbRef.child(lobbyId!!).child("players").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listOfPlayers.clear()
                    for (item in snapshot.children) {
                        listOfPlayers.add(item.getValue(Player::class.java)!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkStatus() {
            dbRef.child(lobbyId!!).child("state").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("GAME_FLOW", "Status is ${snapshot.value}!")
                    if (snapshot.value != null) status = snapshot.value as String
                    if (snapshot.value == "IN_PROGRESS") handleNextPlayer()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkCurrentPlayer() {
            dbRef.child(lobbyId!!).child("currentPlayer").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(
                        "GAME_FLOW", "Current player is ${listOfPlayers.find { it.id == snapshot.value }?.name}!"
                    )
                    if (snapshot.value != "") {
                        currentPlayerId = snapshot.value as String
                    } else if (snapshot.value == "" && status == "IN_PROGRESS") {
                        awakenPlayersIds.clear()
                        handleNextPlayer()
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkChosenPlayer() {
            dbRef.child(lobbyId!!).child("chosenPlayer").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(
                        "GAME_FLOW", "Chosen player is ${listOfPlayers.find { it.id == snapshot.value }?.name}!"
                    )
                    chosenPlayerId = snapshot.value as String
                    if (chosenPlayerId != "") handleChoice()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkVotesGiven() {
            dbRef.child(lobbyId!!).child("voted").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("GAME_FLOW", "${snapshot.value} players already voted")
                    if (snapshot.value.toString().toInt() == listOfPlayers.size) {
                        Handler(Looper.getMainLooper()).postDelayed({ checkVotingResults() }, 1000)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkVotingResults() {
            dbRef.child(lobbyId!!).child("voting").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.v("GAME_FLOW", "Voting result is ${snapshot.value}")
                    votingResult = snapshot.value as Long
                    showConfirmButton = true
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkDueledPlayer() {
            dbRef.child(lobbyId!!).child("dueledPlayer").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(
                        "GAME_FLOW", "Dueled player is ${listOfPlayers.find { it.id == snapshot.value }?.name}!"
                    )
                    dueledPlayerId = snapshot.value as String
                    if (dueledPlayerId != "")
                        showVoting = true
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkDuelingPlayer() {
            dbRef.child(lobbyId!!).child("duelingPlayer").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(
                        "GAME_FLOW", "Dueling player is ${listOfPlayers.find { it.id == snapshot.value }?.name}!"
                    )
                    duelingPlayerId = snapshot.value as String
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkPlayerToSearch() {
            dbRef.child(lobbyId!!).child("playerToSearch").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(
                        "GAME_FLOW", "Player to search is ${listOfPlayers.find { it.id == snapshot.value }?.name}!"
                    )
                    playerToSearchId = snapshot.value as String
                    if (playerToSearchId != "")
                        showVoting = true
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        fun reset() {
            listOfPlayers.clear()
            thisPlayerId = null
            chosenPlayerId = ""
            lobbyId = null
        }
    }
}