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
import com.szalik.logic.business.MeetingFlow
import com.szalik.logic.business.User
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.enums.MeetingMode
import com.szalik.logic.entertainment.enums.UserMode
import com.szalik.ui.theme.SzalikTheme
import kotlin.random.Random

val dbRefMeeting = DatabaseConnection.getDatabase().getReference("meetings")
var existsMeeting by mutableStateOf<Boolean?>(null)

@Composable
fun CreateMeetingScreen(navController: NavController) {
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
        if (existsMeeting == false && !transferred) {
            val userId = dbRef.push().key
            val user = User(name = name, id = userId!!)
            MeetingFlow.thisUserId = user.id
            MeetingFlow.setMeetingId(lobbyId)

            dbRefMeeting.child(lobbyId).child("state").setValue("WAIT")
            dbRefMeeting.child(lobbyId).child("users").child(userId).setValue(user)
            transferred = true
            MeetingFlow.isHost = true
            navController.navigate(Screen.LobbyScreen.withArgs(lobbyId, UserMode.HOST.name, MeetingMode.BUSINESS.name))
        } else if (existsMeeting == true) {
            lobbyId = getNewLobbyId()
            checkIfLobbyExists(lobbyId)
            existsMeeting = null
        } else {
            existsMeeting = null
        }
    }


    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp), Arrangement.Center) {
                TextField(
                    value = name,
                    placeholder = {
                        Text(text = "Nazwa uczestnika")
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
                            checkIfLobbyExists(lobbyId)
                            clicked = true

                        } else {
                            Toast.makeText(context, "Podaj swoją nazwę!", Toast.LENGTH_LONG).show()
                        }

                    },
                    modifier = Modifier
                        .padding(40.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Utwórz nowe spotkanie",
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
    return Random.nextInt(100000, 999999).toString()
}

private fun checkIfLobbyExists(id: String) {
    dbRefMeeting.addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.v("CREATE_MEETING_SCREEN", "Check if meeting $id exists")
            existsMeeting = snapshot.hasChild(id)
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })
}