package com.szalik.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szalik.R
import com.szalik.logic.business.MeetingFlow
import com.szalik.logic.entertainment.enums.MeetingMode
import com.szalik.ui.common.KeepScreenOn
import com.szalik.ui.common.Timer
import com.szalik.ui.theme.SzalikTheme

@Composable
fun MeetingScreen() {
    KeepScreenOn()
    val context = LocalContext.current

    Log.i("MEETING_SCREEN", "Recompose, time is ${MeetingFlow.time}")

    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                Arrangement.Top,
                Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Czas do końca spotkania:",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                if (MeetingFlow.time != 0L) {
                    Timer(totalTime = MeetingFlow.time, mode = MeetingMode.BUSINESS)
                }
                Spacer(modifier = Modifier.height(30.dp))

                if (MeetingFlow.currentUserId == MeetingFlow.thisUserId) {
                    Text(
                        text = "Twoja kolej!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (MeetingFlow.equalTime) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Twój limit czasu:",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (MeetingFlow.time != 0L) {
                            Timer(totalTime = (MeetingFlow.time / MeetingFlow.listOfUsers.size), mode = MeetingMode.BUSINESS)
                        }
                    }

                    if (MeetingFlow.raisedHandsList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(30.dp))
                        MeetingFlow.raisedHandsList.forEach {
                            Row(Modifier.fillMaxWidth(), SpaceBetween) {
                                Text(
                                    text = "${it.name} podnosi rękę!",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .weight(0.5f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 24.sp
                                )
                                Button(
                                    onClick = {
                                        MeetingFlow.lowerHand(it.id)
                                    },
                                    modifier = Modifier.weight(0.5f)
                                ) {
                                    Text(
                                        text = "Usuń",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {

                        }
                    ) {
                        Text(
                            text = "Zakończ wypowiedź",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp
                        )
                    }


                } else {
                    Text(
                        text = "Obecny mówca: ${MeetingFlow.listOfUsers.find { it.id == MeetingFlow.currentUserId }?.name}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Następny mówca: ${MeetingFlow.listOfUsers.find { it.id == MeetingFlow.nextUserId }?.name}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    if (MeetingFlow.raisedHandsList.none { it.id == MeetingFlow.thisUserId }) {
                        Button(
                            onClick = {
                                MeetingFlow.raiseHand()
                                Toast.makeText(
                                    context,
                                    "Podniosłeś rękę",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.stop),
                                    contentDescription = "Podniesiona ręka",
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(25.dp)
                                )
                                Text(
                                    text = "Podnieś rękę",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colors.onPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Podniosłeś już rękę",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}