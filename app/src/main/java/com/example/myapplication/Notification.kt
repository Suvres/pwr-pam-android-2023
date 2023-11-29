package com.example.myapplication

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat



class Notification : BroadcastReceiver()
{
    companion object {
        const val notificationID = 1
        const val channelID = "todo"
        const val titleExtra = "titleExtra"
        const val messageExtra = "messageExtra"
        const val iconExtra = "iconExtra"
    }
    override fun onReceive(context: Context, intent: Intent)
    {
        Log.d("POWIADOMIENIA", "ODBIÓR")
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(intent.getIntExtra(iconExtra, R.drawable.ic_notification))
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentTitle(intent.getStringExtra(messageExtra))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

}