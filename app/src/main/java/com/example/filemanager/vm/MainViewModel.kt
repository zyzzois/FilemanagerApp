package com.example.filemanager.vm

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {

    private val _rootPath = MutableLiveData<String>()
    val rootPath: LiveData<String>
        get() = _rootPath


    fun exploreRootPath() {
        _rootPath.value = Environment.getExternalStorageDirectory().path
    }


}