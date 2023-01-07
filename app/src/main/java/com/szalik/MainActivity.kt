package com.szalik

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.getSystemService
import com.szalik.logic.common.VibrateEngine
import com.szalik.logic.entertainment.GameFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GameFlow.testMode = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            VibrateEngine.setVersion(vibratorManager.defaultVibrator)
        } else {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            VibrateEngine.setVersion(vibrator)
        }

        setContent {
            Navigation()
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
}
