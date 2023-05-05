package com.example.filemanager.app

import android.app.Application
import com.example.filemanager.di.DaggerApplicationComponent

class FileManagerApp: Application() {
    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}