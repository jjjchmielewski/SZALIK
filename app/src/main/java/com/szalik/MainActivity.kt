package com.szalik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.szalik.logic.entertainment.GameFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GameFlow.testMode = true
        setContent {
            Navigation()
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
}
