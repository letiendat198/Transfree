package com.transfree.ui.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.transfree.file_io.FileHelper

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