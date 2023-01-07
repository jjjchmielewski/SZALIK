package com.szalik.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Tile(
    name: String,
    color: Color,
    navController: NavController? = null,
    destination: String? = null
) {
    Card(
        modifier = Modifier
            .width(175.dp)
            .height(200.dp)
            .padding(10.dp)
            .clickable {
                if (destination != null) {
                    navController?.navigate(destination)
                }
            },
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp,
        backgroundColor = color
    ) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(
                text = name,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}