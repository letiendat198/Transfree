package com.transfree.notification

import android.Manifest
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.transfree.server.ServerService

class RequestNotificationPermission(private val context: ComponentActivity, private val callback: () -> Unit) {
    fun request(){
        CreateChannels(context)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU){
            requestNotificationPermission()
        }
        else{
            callback()
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission() {
        val requestPermissionLauncher = context.registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted ->
            if (isGranted){
                callback()
            }
            else{
                Toast.makeText(context, "Notification Permission Needed", Toast.LENGTH_SHORT).show()
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}