package com.transfree.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.transfree.SendActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

class DeviceBoxComponent(private val context: Context,
                         private val name: String,
                         private val port: Int,
                         private val host: String
){
    @Composable
    fun DeviceBox(){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp).clickable {
                val sendIntent = Intent(context, SendActivity::class.java)
                sendIntent.putExtra("ip", host)
                sendIntent.putExtra("port", port)
                context.startActivity(sendIntent)
            }.fillMaxWidth()
        ){
            Text("OS")
            Column (
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                Text("Device: $name")
                Text("IP: $host")
                Text("Port: $port")
            }
        }
    }
}