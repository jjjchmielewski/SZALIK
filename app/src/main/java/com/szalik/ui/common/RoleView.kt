package com.szalik.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.Player
import com.szalik.logic.entertainment.enums.Fraction
import com.szalik.logic.entertainment.enums.Role

@Composable
fun RoleView(player: Player) {
    val fractionColor = when (player.card!!.role!!.fraction) {
        Fraction.CITY -> Color(0xFF8D7705)
        Fraction.BANDITS -> Color(0xFF6D6C6B)
        Fraction.INDIANS -> Color(0xFF680609)
        Fraction.ALIENS -> Color(0xFF045A01)
    }

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(fractionColor)
    ) {
        Text(
            text = "Frakcja: ${player.card!!.role!!.fraction.polishName}",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = player.card!!.role!!.description,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth()
        )
    }
    if (player.card?.actionsLeftCounter != 999) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Masz jeszcze ${player.card?.actionsLeftCounter} akcji do wykorzystania",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    if (player.card?.hasTotem == true) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Posiadasz posążek!",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    if (player.card?.isBlackmailed == true) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Jesteś szantażowany przez ${GameFlow.listOfPlayers.find { it.card?.role == Role.BLACKMAILER }?.name}",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    if (player.card?.isSeduced == true) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Zostałeś uwiedziony przez ${GameFlow.listOfPlayers.find { it.card?.role == Role.SEDUCER }?.name}",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    if (player.card?.isJailed == true) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Jesteś w areszcie!",
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}