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
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.Player
import com.szalik.logic.entertainment.enums.MeetingMode
import com.szalik.logic.entertainment.enums.UserMode
import com.szalik.ui.theme.SzalikTheme

@Composable
fun JoinMeetingScreen(navController: NavController) {
    var meetingId by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    var exists: Boolean? = null
    var meetingStatus = ""
    val dbRef = DatabaseConnection.getDatabase().getReference("meetings")

    dbRef.addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.v("JOIN_MEETING_SCREEN + ${System.identityHashCode(this)}", "Check if meeting $meetingId exists")
            exists = snapshot.hasChild(meetingId)
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

    dbRef.child(meetingId).child("state").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.i("JOIN_MEETING_SCREEN + ${System.identityHashCode(this)}", "Status is ${snapshot.value}!")
            if (snapshot.value  != null)
                meetingStatus = snapshot.value as String
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })



    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp), Arrangement.Center) {
                TextField(
                    value = meetingId,
                    placeholder = {
                        Text("Kod spotkania")
                    },
                    onValueChange = {
                        meetingId = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(10.dp))

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

                        if (meetingId != "" && meetingId.length == 6) {
                            if (name != "") {
                                loop@ while (true) {
                                    when (exists) {
                                        null -> {
                                            continue@loop
                                        }
                                        true -> {
                                            if (meetingStatus == "WAIT") {
                                                MeetingFlow.setMeetingId(meetingId)
                                                val userId = dbRef.push().key
                                                val user = User(name = name, id = userId!!)
                                                MeetingFlow.thisUserId = user.id
                                                dbRef.child(meetingId).child("users").child(userId)
                                                    .setValue(user)
                                                navController.navigate(
                                                    Screen.LobbyScreen.withArgs(
                                                        meetingId,
                                                        UserMode.GUEST.name,
                                                        MeetingMode.BUSINESS.name
                                                    )
                                                )
                                                break@loop
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "To spotkanie już się rozpoczęło!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                break@loop
                                            }
                                        }
                                        false -> {
                                            Toast.makeText(
                                                context,
                                                "Nie znaleziono spotkania dla podanego kodu",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            break@loop
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Podaj swoją nazwę!", Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Niepoprawny format kodu spotkania!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Dołącz do spotkania",
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