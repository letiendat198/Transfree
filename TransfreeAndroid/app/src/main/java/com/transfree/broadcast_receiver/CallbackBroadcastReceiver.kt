package com.transfree.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.transfree.server.ServerService

class CallbackBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getBooleanExtra("Status", false)

        val serverIntent = Intent(context, ServerService::class.java)
        serverIntent.putExtra("Confirm", status)
        serverIntent.putExtra("Type", "CONFIRM")
        context.startService(serverIntent)

        val notificationID = intent.getIntExtra("ID",1)
        with (NotificationManagerCompat.from(context)){
            cancel(notificationID)
        }

    }
}