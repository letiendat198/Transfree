package com.transfree.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.transfree.server.ServerService

class StopServerBroadcastReceiver: BroadcastReceiver() {
    private final var TAG = "STOP_BROADCAST_RECEIVER"

    override fun onReceive(context: Context, intent: Intent) {
        val notificationID = intent.getIntExtra("ID",-1)
        with (NotificationManagerCompat.from(context)){
            cancel(notificationID)
        }
        Log.d(TAG, "Received")
        context.stopService(Intent(context, ServerService::class.java))
    }
}