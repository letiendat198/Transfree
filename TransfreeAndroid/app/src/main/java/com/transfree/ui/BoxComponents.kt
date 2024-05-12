package com.transfree.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.transfree.file_io.FileHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

class DeviceBoxComponent(private val context: Context,
                         private val name: String,
                         private val port: Int,
                         private val host: InetAddress){
    private var ip = mutableStateOf("")
    init {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                ip.value = host.hostName
            }
        }
    }
    @Composable
    fun DeviceBox(){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp).clickable {
                val sendIntent = Intent(context, SendActivity::class.java)
                sendIntent.putExtra("ip", host.hostAddress)
                sendIntent.putExtra("port", port)
                context.startActivity(sendIntent)
            }.fillMaxWidth()
        ){
            Text("OS")
            Column (
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                val ip by ip
                Text("Device: $name")
                Text("IP: $ip")
                Text("Port: $port")
            }
        }
    }
}


public class FileBoxComponent(private val context: Context, public val uri: Uri) {
    private val status = mutableStateOf("Waiting")
    private var name = ""
    private var size: Long = 0

    init {
        val fileDetails = FileHelper(context, uri)
        name = fileDetails.getName()
        size = fileDetails.getSize()
    }

    @Composable
    fun FileBox(){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ){
            Text("Image")
            Column (
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                Text("File name: $name")
                val sizeInMb = Math.round(((size.toDouble()) / (1024*1024)) * 100).toDouble() / 100
                Text("Size: $sizeInMb MB")
                val status by status
                Text("Status: $status")
            }
        }
    }
    public fun updateStatus(status: String){
        this.status.value = status
    }
}
