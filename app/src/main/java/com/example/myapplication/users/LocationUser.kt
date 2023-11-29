package com.example.myapplication.users

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

data class LocationUser(
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var user: String = "",
    var saveDate: Timestamp = Timestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
)