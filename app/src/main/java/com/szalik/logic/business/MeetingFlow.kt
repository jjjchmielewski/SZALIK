package com.szalik.logic.business

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.szalik.logic.common.database.DatabaseConnection

class MeetingFlow {
    companion object {
        val listOfUsers = mutableStateListOf<User>()
        var thisUserId: String? = null
        var currentUserId by mutableStateOf("")
        var nextUserId by mutableStateOf("")
        var status by mutableStateOf("")
        var raisedHandsList = mutableStateListOf<User>()
        var isHost = false
        var firstUserId = ""

        var time by mutableStateOf(0L)
        var equalTime by mutableStateOf(false)
        var ttsMessage by mutableStateOf<String?>(null)
        var endOfMeeting by mutableStateOf(false)

        var userInMeeting = false
        private var meetingId: String? = null
        private var dbRef = DatabaseConnection.getDatabase().getReference("meetings")

        fun setMeetingId(meetingId: String): Boolean {
            this.meetingId = meetingId
            checkStatus()
            getUsers()
            return true
        }

        fun reset() {
            listOfUsers.clear()
            thisUserId = null
            currentUserId = ""
            nextUserId = ""
            status = ""
            raisedHandsList.clear()
            isHost = false
            firstUserId = ""
            time = 0L
            equalTime = false
            ttsMessage = ""
            endOfMeeting = false
            userInMeeting = false
            meetingId = null
        }

        fun prepareMeetingByHost() {
            dbRef.child(meetingId!!).child("state").setValue("PREPARING")
        }

        fun start() {
            firstUserId = listOfUsers.first().id
            dbRef.child(meetingId!!).child("currentUser").setValue(listOfUsers.first().id)
            dbRef.child(meetingId!!).child("nextUser").setValue(listOfUsers[1].id)
            dbRef.child(meetingId!!).child("time").setValue(time)
            dbRef.child(meetingId!!).child("equalTime").setValue(equalTime)
            dbRef.child(meetingId!!).child("state").setValue("STARTED")
            checkEndOfMeeting()
            checkTime()
            checkEqualTime()
            checkNextUser()
            checkCurrentUser()
            checkRaisedHands()
        }

        fun startAsGuest() {
            checkEndOfMeeting()
            checkTime()
            checkEqualTime()
            checkNextUser()
            checkCurrentUser()
            checkRaisedHands()
        }

        fun raiseHand() {
            val user = listOfUsers.find { it.id == thisUserId }!!
            dbRef.child(meetingId!!).child("raisedHands").child(user.id).setValue(user)
        }

        fun lowerHand(id: String) {
            dbRef.child(meetingId!!).child("raisedHands").child(id).setValue(null)
        }

        fun nextUser() {
            if (nextUserId != "") {
                dbRef.child(meetingId!!).child("raisedHands").setValue(null)
                dbRef.child(meetingId!!).child("currentUser").setValue(nextUserId)
            } else {
                dbRef.child(meetingId!!).child("endOfMeeting").setValue(true)
            }

        }

        private fun getUsers() {
            dbRef.child(meetingId!!).child("users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listOfUsers.clear()
                    for (item in snapshot.children) {
                        listOfUsers.add(item.getValue(User::class.java)!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkStatus() {
            dbRef.child(meetingId!!).child("state").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("MEETING_FLOW", "Status is ${snapshot.value}!")
                    if (snapshot.value != null) status = snapshot.value as String
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkCurrentUser() {
            dbRef.child(meetingId!!).child("currentUser").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("MEETING_FLOW", "Current user is ${snapshot.value}!")
                    if (snapshot.value != null && snapshot.value != currentUserId) {
                        currentUserId = snapshot.value as String
                        if (isHost && currentUserId != firstUserId) {
                            val first = listOfUsers.first()
                            listOfUsers.removeFirst()
                            listOfUsers.add(first)
                            if (firstUserId == listOfUsers[1].id) {
                                dbRef.child(meetingId!!).child("nextUser").setValue("")
                            } else {
                                dbRef.child(meetingId!!).child("nextUser").setValue(listOfUsers[1].id)
                            }
                        }
                        if (currentUserId == thisUserId) {
                            ttsMessage = "Następny mówca - ${listOfUsers.find { it.id == thisUserId }?.name}"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkNextUser() {
            dbRef.child(meetingId!!).child("nextUser").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("MEETING_FLOW", "Next user is ${snapshot.value}!")
                    if (snapshot.value != null)
                        nextUserId = snapshot.value as String
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkTime() {
            dbRef.child(meetingId!!).child("time").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("MEETING_FLOW", "Time is ${snapshot.value}!")
                    if (snapshot.value != null)
                        time = snapshot.value as Long
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkEqualTime() {
            dbRef.child(meetingId!!).child("equalTime").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("MEETING_FLOW", "Is time equal: ${snapshot.value}!")
                    if (snapshot.value != null)
                        equalTime = snapshot.value as Boolean
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkRaisedHands() {
            dbRef.child(meetingId!!).child("raisedHands").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("MEETING_FLOW", "Raised hands: ${snapshot.value}!")
                    raisedHandsList.clear()
                    for (item in snapshot.children) {
                        raisedHandsList.add(item.getValue(User::class.java)!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkEndOfMeeting() {
            dbRef.child(meetingId!!).child("endOfMeeting").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("MEETING_FLOW", "Is meeting ended: ${snapshot.value}!")
                    if (snapshot.value != null && snapshot.value != "")
                        endOfMeeting = snapshot.value as Boolean
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}