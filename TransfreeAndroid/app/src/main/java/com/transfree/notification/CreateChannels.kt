package com.transfree.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Context
import androidx.core.content.getSystemService

class CreateChannels (context: Context) {
    init {
        val name = "Confirm Channel"
        val descriptionText = "Send confirmation when other devices request to send you files"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("CONFIRM", name, importance).apply { description = descriptionText }

        val notificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    init {
        val name = "Receive Channel"
        val descriptionText = "Show file receiving progress"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("RECEIVE", name, importance).apply { description = descriptionText }

        val notificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    init {
        val name = "Persistence Channel"
        val descriptionText = "Notify whether server is running"
        val importance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel("PERSIST", name, importance).apply { description = descriptionText }

        val notificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}