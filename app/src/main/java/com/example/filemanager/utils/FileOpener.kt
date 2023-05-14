package com.example.filemanager.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.filemanager.utils.Constants.PROVIDER
import com.example.filemanager.utils.Constants.SHARE_FILE
import java.io.File
import java.io.IOException

class FileOpener {
    @Throws(IOException::class)
    fun openFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + PROVIDER,
            file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        if (uri.toString().contains(".doc")) {
            intent.setDataAndType(uri, "application/msword")
        } else if (uri.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf")
        } else if (uri.toString().contains(".mp3") || uri.toString().contains(".wav")) {
            intent.setDataAndType(uri, "audio/x-wav")
        } else if (uri.toString().lowercase().contains(".jpeg")
            || uri.toString().lowercase().contains(".jpg")
            || uri.toString().lowercase().contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg")
        } else if (uri.toString().contains(".mp4")) {
            intent.setDataAndType(uri, "video/*")
        } else {
            intent.setDataAndType(uri, "*/*")
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + PROVIDER,
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = context.contentResolver.getType(uri)
        }
        context.startActivity(Intent.createChooser(intent, SHARE_FILE))
    }

}