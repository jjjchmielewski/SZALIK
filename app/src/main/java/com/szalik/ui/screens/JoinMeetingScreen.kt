package com.szalik.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.ui.theme.SzalikTheme

@Composable
fun JoinMeetingScreen(navController: NavController) {
    var meetingId by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }

    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize(), Arrangement.Top) {
                Spacer(modifier = Modifier.fillMaxHeight(0.2f))

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

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val dbRef = DatabaseConnection.getDatabase().getReference("lobbies/$meetingId")
                        val playerId = dbRef.push().key
                        dbRef.child(playerId!!).setValue(name)
                        navController.navigate(Screen.LobbyScreen.withArgs(meetingId))
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Dołącz do spotkania",
                        color = Color.Black,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}