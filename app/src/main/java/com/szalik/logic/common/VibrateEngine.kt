package com.szalik.logic.common

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class VibrateEngine {
    companion object {
        private var vibrator: Vibrator? = null

        fun setVersion(vibrator: Vibrator) {
            this.vibrator = vibrator
        }

        fun vibrate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(400)
            }
        }
    }
}