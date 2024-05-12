package com.transfree

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.transfree.client.ClientThread
import com.transfree.ui.FileBoxComponent
import com.transfree.ui.theme.TransfreeTheme
import com.transfree.utils.FileStatus.STATUS


class SendActivity: ComponentActivity() {
    private val TAG = "SEND_ACTIVITY"
    private val expanded = mutableStateOf(false)
    private val fileBoxList = mutableStateListOf<FileBoxComponent>()
    private val fileList = mutableListOf<Uri>()

    private var ip = ""
    private var port = 0

    private val getFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> onFileResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ip = intent.getStringExtra("ip").toString()
        port = intent.getIntExtra("port", 0)
        Log.d(TAG, ip)
        setContent {
            TransfreeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SendView()
                }
            }
        }
    }

    private fun onFileAdd(){
        val fileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
        }
        getFile.launch(fileIntent)
    }

    @SuppressLint("Recycle")
    private fun onFileResult(result: ActivityResult){
        Log.d(TAG, "File chosen")
        if (result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            if (data != null) {
                val uri = data.data
                Log.d(TAG, uri.toString())
                if (uri != null && uri.path != null){
                    fileBoxList.add(FileBoxComponent(this, uri))
                    fileList.add(uri)
                }
            }
        }
    }

    private fun onSend(){
        Log.d(TAG, "Send button clicked")
        val clientThread = ClientThread(this, this.ip, port)
        clientThread.addFiles(fileList)
        clientThread.addCallback(this::onCallback)
        val thread = Thread(clientThread)
        thread.start()
    }

    private fun onCallback(uri: Uri, status: STATUS){
        Log.d(TAG, "Callback")
        var isRemove = false;
        for (fileBox in fileBoxList){
            if (fileBox.uri == uri){
                if (status == STATUS.START){
                    fileBox.updateStatus("Sending")
                }
                else if (status == STATUS.SENT){
                    fileBox.updateStatus("Sent")
                    isRemove = true
                }
                else if (status == STATUS.FAILED){
                    fileBox.updateStatus("Failed")
                    isRemove = true
                }
            }
        }
        if (isRemove){
            val fileIterator = fileList.iterator()
            while (fileIterator.hasNext()){
                val currentUri = fileIterator.next()
                if (currentUri == uri){
                    fileIterator.remove()
                }
            }
        }
    }

    @Composable
    fun SendView(){
        val fileBoxListSnap = remember { fileBoxList }
        Scaffold(
            topBar = {
                SendTopBar()
            },
            floatingActionButton = {
                SendButton()
            }
        )
        { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding)
            ) {
                items(fileBoxListSnap.toList()){
                    fileBoxCompo -> fileBoxCompo.FileBox()
                }
            }
        }
    }

    @Composable
    fun SendButton(){
        ExtendedFloatingActionButton(onClick = { onSend() }) {
            Icon(Icons.Filled.Send, "Send Icon")
            Text("Send")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SendTopBar(){
        val context = LocalContext.current
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { (context as Activity).finish() }) {
                    Icon(Icons.Filled.ArrowBack, "Back" )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text("Send files")
            },
            actions = {
                IconButton(onClick = { onFileAdd() }) {
                    Icon(Icons.Filled.Add, "Add file")
                }
                IconButton(onClick = {expanded.value = true}) {
                    Icon(Icons.Filled.MoreVert, "Settings")
                }
                DropDown()
            }
        )
    }

    @Composable
    fun DropDown(){
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = { /*TODO*/ }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SendViewPreview() {
        TransfreeTheme {
            SendView()
        }
    }
}



