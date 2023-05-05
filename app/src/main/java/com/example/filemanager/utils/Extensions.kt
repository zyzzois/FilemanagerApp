package com.example.filemanager.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.domain.entity.FileEntity
import java.io.File

private var permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

fun AppCompatActivity.isStoragePermissionGranted(): Boolean {
    return ActivityCompat.checkSelfPermission(
        this, permission[0]
    ) == PackageManager.PERMISSION_GRANTED
}

fun AppCompatActivity.showStoragePermissionDialog(requestCode: Int) {
    ActivityCompat.requestPermissions(this, permission, requestCode)
}

fun FileEntity.folderLength() = this.file.listFiles()?.size ?: 1

