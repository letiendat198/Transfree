package com.transfree.server

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.transfree.notification.PersistenceNotification
import com.transfree.server.Server

class ServerService: Service() {
    private var thread:  Thread? = null
    private var server: Server? = null
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        server = Server(this, 1908)
        thread = Thread(server)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val type = intent?.getStringExtra("Type")
        val confirmation = intent?.getBooleanExtra("Confirm", false)

        if (!isRunning){
            thread?.start()
            isRunning = true
            PersistenceNotification(this).issue()
            Toast.makeText(this, "Transfree server started", Toast.LENGTH_SHORT).show()
        }
        if (type=="CONFIRM"){
            if (confirmation != null){
                Log.d("SERVER_SERVICE", confirmation.toString())
                server?.onConfirmCallback(confirmation)
            }
        }


        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        isRunning = false
        Toast.makeText(this, "Transfree Server stopped", Toast.LENGTH_SHORT).show()
        with (NotificationManagerCompat.from(applicationContext)){
            cancel(-1)
        }
    }
}