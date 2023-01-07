package com.szalik.logic.common

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TTSEngine {
    companion object {
        private var ttsEngine: TextToSpeech? = null

        private fun initTTS(context: Context) {
            ttsEngine = TextToSpeech(context) {
                if (it == TextToSpeech.SUCCESS) {
                    ttsEngine!!.setLanguage(Locale("pl_PL"))
                }
            }
        }

        fun getTTS(context: Context): TextToSpeech {
            return if (ttsEngine == null) {
                initTTS(context)
                ttsEngine!!
            } else {
                ttsEngine!!
            }
        }
    }
}