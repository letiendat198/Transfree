package com.transfree.file_io

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import com.transfree.utils.Settings
import kotlinx.coroutines.runBlocking

class RequestFolderPermission(private val context: ComponentActivity) {
    private val TAG = "FOLDER_PERM_REQ"
    fun requestSaveFolder(){
        // Check if a save Uri has already existed
        var isRequested: Boolean? = false
        val savePathKey = stringPreferencesKey("savePath")
        runBlocking {
            isRequested = Settings(context).exists(savePathKey)
        }
        // If an Uri exists, check if it has valid permission. If not, ask again
        if (isRequested == true) {
            val uri = Settings(context).blockingReadKey(savePathKey)
            val hasPerm = context.checkUriPermission(Uri.parse(uri), android.os.Process.myPid(), android.os.Process.myUid(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (hasPerm == PackageManager.PERMISSION_GRANTED) return
        }
        // Ask permission
        val getFile = context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result -> onFolderGranted(result)
        }
        val folderIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        getFile.launch(folderIntent)
    }

    private fun onFolderGranted(result: ActivityResult){
        Log.d(TAG, "Directory chosen")
        if (result.resultCode == Activity.RESULT_OK){
            val path: Intent? = result.data
            if (path != null) {
                val uri = path.data
                Log.d(TAG, uri.toString())
                if (uri != null && uri.path != null){
                    val savePathKey = stringPreferencesKey("savePath")
                    runBlocking {
                        Settings(context).writeKey(savePathKey, uri.toString())
                    }
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
            }
        }
    }
}