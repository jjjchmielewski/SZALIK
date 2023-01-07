package com.szalik.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.szalik.R
import com.szalik.ui.common.Tile
import com.szalik.ui.theme.SzalikTheme

@Composable
fun GameScreen(navController: NavController) {
    SzalikTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(Modifier.fillMaxSize(), Arrangement.Center) {
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))

                Image(
                    painter = painterResource(id = R.drawable.team),
                    contentDescription = "Grupa znajomych",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.fillMaxHeight(0.1f))

                Row(Modifier.fillMaxWidth(), Arrangement.Center){
                    Text(
                        text = "Gra towarzyska",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
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
                        name = "Utwórz grę",
                        color = MaterialTheme.colors.primaryVariant,
                        navController = navController,
                        destination = Screen.CreateGameScreen.route
                    )
                    Tile(
                        name = "Dołącz do gry",
                        color = MaterialTheme.colors.primary,
                        navController = navController,
                        destination = Screen.JoinGameScreen.route
                    )
                }
            }
        }
    }
}