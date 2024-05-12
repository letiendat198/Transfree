package com.transfree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.transfree.file_io.RequestFolderPermission
import com.transfree.notification.RequestNotificationPermission
import com.transfree.server.ServerService
import com.transfree.service_discovery.ServiceDiscovery
import com.transfree.ui.DeviceBoxComponent
import com.transfree.ui.theme.TransfreeTheme

class MainActivity : ComponentActivity() {
    private var TAG: String = "MAIN_ACTIVITY"
    private val deviceBoxList = mutableStateListOf<DeviceBoxComponent>()

    private var serviceDiscovery: ServiceDiscovery? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RequestNotificationPermission(this){
            startService(Intent(this, ServerService::class.java))
        }.request()

        RequestFolderPermission(this).requestSaveFolder()

        serviceDiscovery = ServiceDiscovery(this){name, port, host ->
            Log.d(TAG, "A new device found!")
            deviceBoxList.add(DeviceBoxComponent(this, name, port, host))
        }
        serviceDiscovery!!.registerService(1908)
        serviceDiscovery!!.discoverService()

        setContent {
            TransfreeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
    }
    @Composable
    fun MainView(){
        val deviceBoxListSnapshot = remember { deviceBoxList }
        Scaffold(
            topBar = {
                MainTopBar()
            },
            floatingActionButton = {
                ScanButton()
            }
        )
        { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding)
            ) {
                items(deviceBoxListSnapshot.toList()){
                    deviceBoxComponent -> deviceBoxComponent.DeviceBox()
                }
            }
        }
    }

    @Composable
    fun ScanButton(){
        FloatingActionButton(onClick = {  }) {
            Icon(Icons.Filled.Refresh, "Scan Icon")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainTopBar(){
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text("Scan for devices")
            },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.MoreVert, "Settings")
                }
            }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun MainViewPreview() {
        TransfreeTheme {
            MainView()
        }
    }
}

