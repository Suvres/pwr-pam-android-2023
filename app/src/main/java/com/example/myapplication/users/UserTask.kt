package com.example.myapplication.users

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.type.DateTime
import kotlinx.android.parcel.Parcelize
import java.sql.Time
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import java.util.UUID

@Parcelize
data class UserTask(
    var uid: String = UUID.randomUUID().toString(),
    var user: String = "",
    var type: TaskType = TaskType.CLICK,
    var status: TaskStatus = TaskStatus.TODO,
    var startDate: Timestamp = Timestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())),
    var description: String = "",
    var result: String = "",
    var closeDate: Timestamp? = null
): Parcelable