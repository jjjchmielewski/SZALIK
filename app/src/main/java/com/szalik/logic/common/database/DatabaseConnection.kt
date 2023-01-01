package com.szalik.logic.common.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.FileInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DatabaseConnection {
    companion object {
        private var database: FirebaseDatabase? = null

        private fun initiate() {
            database = Firebase.database("DATABASE_URL_PLACEHOLDER")
        }

        private fun removeOldData() {
            val ref = database?.getReference("lobbies")
        }

        fun getDatabase(): FirebaseDatabase {
            return if (database != null)
                database as FirebaseDatabase
            else {
                initiate()
                database as FirebaseDatabase
            }
        }
    }
}