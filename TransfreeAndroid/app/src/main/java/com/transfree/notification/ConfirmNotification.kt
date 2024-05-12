package com.transfree.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.transfree.R
import com.transfree.broadcast_receiver.CallbackBroadcastReceiver

class ConfirmNotification(private val context: Context, private val deviceName: String) {
    private val builder = NotificationCompat.Builder(context, "CONFIRM");
    private var notificationID: Int = 1

    init {
        val acceptIntent = Intent(context, CallbackBroadcastReceiver::class.java)
        acceptIntent.putExtra("Status", true)
        acceptIntent.putExtra("ID", notificationID)
        val refuseIntent = Intent(context, CallbackBroadcastReceiver::class.java)
        refuseIntent.putExtra("Status", false)
        refuseIntent.putExtra("ID", notificationID)

        val pendingAcceptIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pendingRefuseIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        this.builder.setContentTitle("Transfree")
            .setContentText("$deviceName wants to send you files")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(R.drawable.ic_launcher_foreground, "ACCEPT", pendingAcceptIntent)
            .addAction(R.drawable.ic_launcher_foreground, "REFUSE", pendingRefuseIntent)
    }

    public fun issue(){
        with(NotificationManagerCompat.from(context)){
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Notification Permission Needed", Toast.LENGTH_SHORT).show()
                return@with
            }
            notify(notificationID, builder.build())
        }
    }
}