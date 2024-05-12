package com.transfree.file_io

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import java.io.InputStream
import java.io.OutputStream

class FileHelper(private val context: Context, private val uri: Uri) {

    val returnCursor = context.contentResolver.query(uri, null, null, null, null);

    fun getName(): String {
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst();
        return returnCursor.getString(nameIndex)
    }

    fun getSize(): Long{
        val sizeIndex = returnCursor!!.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        return returnCursor.getLong(sizeIndex)
    }

    fun getInputStream(): InputStream? {
        return context.getContentResolver().openInputStream(uri);
    }

    fun getOutputStream(): OutputStream?{
        return context.contentResolver.openOutputStream(uri)
    }

    companion object{
        fun createFileFromTreeUri(context: Context, uri: Uri, file: String): Uri?{
            val documentFile = DocumentFile.fromTreeUri(context, uri)
            return documentFile?.createFile("application/notrecognize", file)?.uri
        }
    }
}