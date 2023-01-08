package com.szalik.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.enums.MeetingMode
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
    } else {
        if (currentTime > 0) {
            val hours = (currentTime / 3600000).toInt()
            val minutes = ((currentTime - hours * 3600000) / 60000).toInt()
            val seconds = ((currentTime - hours * 3600000) - minutes * 60000) / 1000

            var text = ""

            if (hours > 0)
                text += "0$hours:"
            if (minutes in 1..9)
                text += "0$minutes:"
            else if (minutes > 9)
                text += "$minutes:"
            if (seconds in 1..9)
                text += "0$seconds"
            else if (seconds > 9)
                text += "$seconds"
            else
                text += seconds

            Text(
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.primaryVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            //koniec spotkania
        }
    }


    if (!isTimerRunning) {
        isTimerRunning = true
    }

}