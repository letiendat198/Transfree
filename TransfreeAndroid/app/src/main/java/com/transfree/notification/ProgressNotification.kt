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

class ProgressNotification(private val context:Context, private val name: String) {
    private val builder = NotificationCompat.Builder(context, "RECEIVE");
    private val notificationID = 0

    init {
        this.builder.setContentTitle("Receiving file")
            .setContentText(name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, 0, false)
    }

    public fun updateProgress(prog: Int){
        this.builder.setProgress(100,prog,false)
        issue()
    }

    public fun onFinished(){
        this.builder.setContentTitle("File received")
            .setContentText(name)
            .setProgress(0,0,false)
        issue()
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