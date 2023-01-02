package com.szalik.logic.entertainment

import android.util.Log
import androidx.compose.runtime.*
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
        val listOfPlayers = mutableStateListOf<Player>()
        val awakenPlayersIds = mutableStateListOf<String>()
        var thisPlayerId: String? = null
        var status by mutableStateOf("")
        var playerInGame = false
        var currentPlayerId by mutableStateOf("")
        var showActionQuestion by mutableStateOf(false)
        var showChoiceList by mutableStateOf(false)
        var showIdentity by mutableStateOf(false)
        var showTotemLocation by mutableStateOf(false)
        var showConfirmButton by mutableStateOf(false)
        var chosenPlayerId: String = ""
        var dayNumber = 0
        val eliminatedLastNight = mutableListOf<String>()
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
            checkCurrentPlayer()
            checkChosenPlayer()
            nightZero()
            for (player in listOfPlayers) {
                Log.i("GAME_FLOW", player.card?.role?.polishName!!)
            }
        }

        fun prepareGameByGuest() {
            checkCurrentPlayer()
            checkChosenPlayer()
            nightZero()
        }

        fun startGame() {
            Log.i("GAME_FLOW", "Starting game")
            if (isHost)
                dbRef.child(lobbyId!!).child("state").setValue("IN_PROGRESS")
            handleNextPlayer()
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
            rolesQueue.add(Role.COQUETTE)
            rolesQueue.add(Role.SEDUCER)
            rolesQueue.add(Role.SHERIFF)
            rolesQueue.add(Role.PRIEST)
            rolesQueue.add(Role.WARLORD)
            rolesQueue.add(Role.BLACKMAILER)
        }

        private fun night() {
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
            rolesQueue.add(Role.SHAMANESS)
            rolesQueue.add(Role.WARRIOR)
            rolesQueue.add(Role.BINOCULARS_EYE)
            rolesQueue.add(Role.MIND_EATER)
            rolesQueue.add(Role.GREAT_ALIEN)
            rolesQueue.add(Role.GREEN_TENTACLE)
            rolesQueue.add(Role.PURPLE_SUCTION)
        }

        private fun day() {
            indiansKillCounter = 0
            indiansTotemTakeover = false
            sharedIdentity = null
            listOfPlayers.forEach {
                if (it.card!!.isJailed)
                    it.card!!.isJailed = false
                if (it.card!!.isDrunk)
                    it.card!!.isDrunk = false
                if (it.card!!.isProtected)
                    it.card!!.isProtected = false
                if (it.card!!.isPlaying)
                    it.card!!.isPlaying = false
            }

            eliminatedLastNight.forEach {
                //if hasTotem
            }

            //night()
        }


        private fun handleNextPlayer() {
            Log.i("GAME_FLOW", "Role queue:")
            rolesQueue.forEach {
                Log.i("ROLES_QUEUE", it.name)
            }
            if (rolesQueue.isNotEmpty()) {
                val nextPlayer =
                    listOfPlayers.find { it.card?.role == rolesQueue.first() && it.card?.isJailed == false && it.card?.isDrunk == false && it.card?.isPlaying == false }
                if (nextPlayer != null) {
                    if (nextPlayer.card?.role == Role.LONELY_COYOTE && listOfPlayers.filter { it.card?.role?.fraction == Fraction.INDIANS }.size > 1) {
                        rolesQueue.removeFirst()
                        handleNextPlayer()
                    }

                    if (nextPlayer.card?.role == Role.BURNING_RAGE && !indiansTotemTakeover) {
                        rolesQueue.removeFirst()
                        handleNextPlayer()
                    }

                    when (nextPlayer.card?.actionsLeftCounter) {
                        999 -> {
                            showChoiceList = true
                        }
                        0 -> {
                            rolesQueue.removeFirst()
                            handleNextPlayer()
                        }
                        else -> {
                            showActionQuestion = true
                        }
                    }

                    when (nextPlayer.card?.role?.fraction) {
                        Fraction.BANDITS -> {
                            listOfPlayers.filter { it.card?.role?.fraction == Fraction.BANDITS && it.card?.isJailed == false && it.card?.isDrunk == false && it.card?.isPlaying == false }
                                .let {
                                    it.forEach { player ->
                                        awakenPlayersIds.add(player.id)
                                    }
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

                        else -> awakenPlayersIds.add(nextPlayer.id)
                    }
                    rolesQueue.removeFirst()
                    dbRef.child(lobbyId!!).child("currentPlayer").setValue(nextPlayer.id)
                } else {
                    rolesQueue.removeFirst()
                    handleNextPlayer()
                }
            } else {
                dayNumber++
                Log.i("GAME_FLOW", "Morning no. $dayNumber")
                day()
            }
        }

        private fun handleChoice() {
            Log.i("GAME_FLOW", "Handling choice for chosen: ${listOfPlayers.find { it.id == chosenPlayerId }?.name}")
            var voiceMessage = ""
            when (listOfPlayers.find { it.id == currentPlayerId }?.card?.role) {
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
                    eliminate(Role.EXECUTIONER, chosenPlayerId)
                }
                Role.DRUNKARD -> {
                    listOfPlayers.find { it.id == chosenPlayerId }?.card?.isDrunk = true
                }
                Role.BODYGUARD -> {
                    listOfPlayers.find { it.id == chosenPlayerId }?.card?.isProtected = true
                    lastProtectedPlayerId = chosenPlayerId
                }
                Role.WARLORD -> {
                    if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS) {
                        passTotemTo(chosenPlayerId)
                    } else {
                        if (listOfPlayers.find { it.id == chosenPlayerId }?.card?.hasTotem == true) {
                            passTotemTo(Role.WARLORD)
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
                Role.CHIEF -> {
                    if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && indiansKillCounter == 0) {
                        indiansKillCounter = 2
                        passTotemTo(chosenPlayerId)
                    } else if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction != Fraction.INDIANS && indiansKillCounter == 0) {
                        indiansKillCounter = 1
                        eliminate(Role.CHIEF, chosenPlayerId)
                    } else if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && indiansKillCounter == 1) {
                        eliminate(Role.CHIEF, chosenPlayerId)
                        indiansKillCounter--
                    } else {
                        eliminate(Role.CHIEF, chosenPlayerId)
                        indiansKillCounter--
                    }
                }
                Role.WARRIOR -> {
                    eliminate(Role.WARRIOR, chosenPlayerId)
                }
                Role.BINOCULARS_EYE -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                }
                Role.SHAMANESS -> {
                    listOfPlayers.find { it.id == chosenPlayerId }?.card?.isPoisoned = true
                }
                Role.LONELY_COYOTE -> {
                    eliminate(Role.LONELY_COYOTE, chosenPlayerId)
                }
                Role.BURNING_RAGE -> {
                    eliminate(Role.BURNING_RAGE, chosenPlayerId)
                }
                Role.SHAMAN -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                }
                Role.GREAT_ALIEN -> {
                    if (listOfPlayers.find { it.id == playerWithTotemId }?.card?.role?.fraction == Fraction.ALIENS) {
                        passTotemTo(chosenPlayerId)
                    } else {
                        if (listOfPlayers.find { it.id == chosenPlayerId }?.card?.hasTotem == true) {
                            passTotemTo(Role.WARLORD)
                        }
                    }
                }
                Role.PURPLE_SUCTION -> {
                    if (listOfPlayers.find { it.id == chosenPlayerId }?.card?.hasTotem == true) {
                        passTotemTo(Role.PURPLE_SUCTION)
                    }
                }
                Role.GREEN_TENTACLE -> {
                    eliminate(Role.GREEN_TENTACLE, chosenPlayerId)
                }
                Role.MIND_EATER -> {
                    sharedIdentity = listOfPlayers.find { it.id == chosenPlayerId }
                }
                else -> {}
            }

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
            listOfPlayers.find { it.card?.role == role }?.card?.hasTotem = true
            listOfPlayers.find { it.id == playerWithTotemId }?.card?.hasTotem = false
            playerWithTotemId = listOfPlayers.find { it.card?.role == role }?.id!!
            if (listOfPlayers.find { it.card?.role == role }?.card?.role?.fraction == Fraction.INDIANS)
                indiansTotemTakeover = true
        }

        private fun passTotemTo(id: String) {
            whereIsTotem()
            listOfPlayers.find { it.id == id }?.card?.hasTotem = true
            listOfPlayers.find { it.id == playerWithTotemId }?.card?.hasTotem = false
            playerWithTotemId = id
            if (listOfPlayers.find { it.id == id }?.card?.role?.fraction == Fraction.INDIANS)
                indiansTotemTakeover = true
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
                for (i in distribution.city downTo 1) {
                    val role = allRoles.filter { it.fraction == Fraction.CITY }.random()
                    allRoles.remove(role)
                    rolesToGive.add(role)
                }
                for (i in distribution.bandits downTo 1) {
                    val role = allRoles.filter { it.fraction == Fraction.BANDITS }.random()
                    allRoles.remove(role)
                    rolesToGive.add(role)
                }
                for (i in distribution.indians downTo 1) {
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

        private fun eliminate(predatorRole: Role, preyId: String) {
            val chosenPlayer = listOfPlayers.find { it.id == preyId }
            var voiceMessage = "Wybrana osoba nie została zabita ponieważ "
            if (chosenPlayer!!.card!!.isJailed)
                voiceMessage += "jest w areszcie"
            else
                if (chosenPlayer.card!!.isProtected)
                    voiceMessage += "jest chroniona"
                else {
                    eliminatedLastNight.add(preyId)
                    listOfPlayers.find { it.id == preyId }?.eliminated = true
                    passTotemTo(predatorRole)
                    voiceMessage = "Wybrana osoba została zabita"
                }
        }

        private fun getPlayers() {
            dbRef.child(lobbyId!!).child("players")
                .addValueEventListener(object : ValueEventListener {
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
            dbRef.child(lobbyId!!).child("state")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.i("GAME_FLOW", "Status is ${snapshot.value}!")
                        if (snapshot.value != null)
                            status = snapshot.value as String
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        private fun checkCurrentPlayer() {
            dbRef.child(lobbyId!!).child("currentPlayer")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.i("GAME_FLOW", "Current player is ${listOfPlayers.find { it.id == snapshot.value }?.name}!")
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
            dbRef.child(lobbyId!!).child("chosenPlayer")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.i("GAME_FLOW", "Chosen player is ${listOfPlayers.find { it.id == snapshot.value }?.name}!")
                        chosenPlayerId = snapshot.value as String
                        if (chosenPlayerId != "")
                            handleChoice()
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