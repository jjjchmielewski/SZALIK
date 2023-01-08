package com.szalik.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.szalik.logic.business.MeetingFlow
import com.szalik.ui.theme.SzalikTheme
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun PrepareMeetingScreen(navController: NavController) {
    var showTime by remember {
        mutableStateOf(true)
    }
    var showTimeDistribution by remember {
        mutableStateOf(true)
    }
    var showQueue by remember {
        mutableStateOf(true)
    }
    var pickerValue by remember {
        mutableStateOf<Hours>(FullHours(0, 0))
    }
    val queue = rememberReorderableLazyListState(onMove = { from, to ->
        MeetingFlow.listOfUsers.apply {
            add(to.index, removeAt(from.index))
        }
    })

    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(20.dp), Arrangement.Center
            )
            {
                if (showTime) {
                    Text(
                        text = "Wybierz czas trwania spotkania",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    HoursNumberPicker(
                        dividersColor = MaterialTheme.colors.primary,
                        hoursRange = 0..9,
                        leadingZero = false,
                        value = pickerValue,
                        onValueChange = {
                            pickerValue = it
                        },
                        hoursDivider = {
                            Text(
                                modifier = Modifier.size(24.dp),
                                textAlign = TextAlign.Center,
                                text = ":"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            MeetingFlow.time = (pickerValue.hours * 3600000 + pickerValue.minutes * 60000).toLong()
                            showTime = false
                            showTimeDistribution = true
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Dalej",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (showTimeDistribution) {
                    Text(
                        text = "Czy chcesz ograniczyć czas wypowiedzi uczestników?",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        onClick = {
                            MeetingFlow.equalTime = true
                            showTimeDistribution = false
                            showQueue = true
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Tak - równy czas wypowiedzi dla każdego",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            MeetingFlow.equalTime = false
                            showTimeDistribution = false
                            showQueue = true
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Nie - brak ograniczeń długości wypowiedzi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (showQueue) {
                    Text(
                        text = "Ustal kolejność wypowiedzi uczestników:",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        state = queue.listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
                            .reorderable(queue)
                            .detectReorderAfterLongPress(queue)
                    ) {
                        items(MeetingFlow.listOfUsers, {it.name}) { item ->
                            ReorderableItem(
                                reorderableState = queue, 
                                key = item.name
                            ) {isDragging ->
                                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                                Column(
                                    modifier = Modifier
                                        .shadow(elevation.value)
                                        .background(MaterialTheme.colors.background)
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = item.name,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colors.onPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }                        
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            showQueue = false
                            MeetingFlow.start()
                            navController.navigate(Screen.MeetingScreen.route)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Przejdź do spotkania",
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
}