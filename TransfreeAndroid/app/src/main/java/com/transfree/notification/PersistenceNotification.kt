package com.transfree.notification

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.transfree.R
import com.transfree.broadcast_receiver.StopServerBroadcastReceiver

class PersistenceNotification(private val context: Context) {
    private val builder = NotificationCompat.Builder(context, "PERSIST");

    init {
        val stopIntent = Intent(context, StopServerBroadcastReceiver::class.java)
        stopIntent.putExtra("ID", -1)

        val pendingStopIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        this.builder.setContentTitle("Transfree server is running")
            .setContentText("Press STOP to stop the server")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(R.drawable.ic_launcher_foreground, "STOP", pendingStopIntent)
    }

    public fun issue(){
        with(NotificationManagerCompat.from(context)){
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Notification Permission Needed", Toast.LENGTH_SHORT).show()
                return@with
            }
            notify(-1, builder.build())
        }
    }
}