package com.example.filemanager.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

@RequiresApi(Build.VERSION_CODES.R)
fun AppCompatActivity.isStoragePermissionGranted(): Boolean {
    return ActivityCompat.checkSelfPermission(
        this, Manifest.permission.MANAGE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun AppCompatActivity.showToast(text: String) =
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()