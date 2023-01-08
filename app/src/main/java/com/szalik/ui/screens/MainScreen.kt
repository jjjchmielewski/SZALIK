package com.szalik.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.szalik.ui.common.Tile
import com.szalik.ui.screens.Screen
import com.szalik.ui.theme.SzalikTheme

@Composable
fun MainScreen(navController: NavController) {
    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp), Arrangement.Center) {
                Row(Modifier.fillMaxWidth(), Arrangement.Center){
                    Text(
                        text = "SZALiK",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.Center){
                    Text(
                        text = "System Zarządzania Aktywnościami Lokalnymi i Komunikacją",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.fillMaxHeight(0.2f))

                Row(Modifier.fillMaxWidth(), Arrangement.Center){
                    Text(
                        text = "Wybierz tryb",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.fillMaxHeight(0.2f))

                Row(
                    Modifier
                        .fillMaxWidth(),
                    Arrangement.SpaceEvenly
                ) {
                    Tile(
                        name = "Biznes",
                        color = MaterialTheme.colors.primaryVariant,
                        navController = navController,
                        destination = Screen.BusinessScreen.route
                    )
                    Tile(
                        name = "Rozrywka",
                        color = MaterialTheme.colors.primary,
                        navController = navController,
                        destination = Screen.GameScreen.route
                    )
                }
            }
        }
    }
}

