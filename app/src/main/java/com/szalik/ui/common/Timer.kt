package com.szalik.ui.common

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.szalik.logic.entertainment.enums.MeetingMode
import com.szalik.logic.common.database.DatabaseConnection
import com.szalik.logic.entertainment.GameFlow
import kotlinx.coroutines.delay

@Composable
fun Timer(totalTime: Long, mode: MeetingMode) {

    var currentTime by remember {
        mutableStateOf(totalTime)
    }

    var isTimerRunning by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if (currentTime > 0 && isTimerRunning) {
            delay(100L)
            currentTime -= 100L
        }
    }
    if (mode == MeetingMode.ENTERTAINMENT) {
        if (currentTime > 0) {
            Text(
                text = "Początek gry za: " + (currentTime / 1000L).toString(),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9C0206)
            )
        } else {
            GameFlow.startGame()
        }
    }


    if (!isTimerRunning) {
        isTimerRunning = true
    }

}