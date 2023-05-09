package com.example.filemanager.vm

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.FileEntity
import com.example.domain.usecase.GetFolderListUseCase
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class FoldersViewModel @Inject constructor(
    private val getFolderListUseCase: GetFolderListUseCase
) : ViewModel() {

    private val defaultPath = Environment.getExternalStorageDirectory().path

    private val _currentPath = MutableLiveData<String>()
    val currentPath: LiveData<String>
        get() = _currentPath

    init {
        _currentPath.value = defaultPath
    }

    private val _folderList = MutableLiveData<List<FileEntity>?>()
    val folderList: LiveData<List<FileEntity>?>
        get() = _folderList


    fun updateList(path: String?) {
        if (path == null || path == DEFAULT_VALUE) {
            viewModelScope.launch {
                _folderList.value = getFolderListUseCase(defaultPath)
            }
        }
        else {
            viewModelScope.launch {
                _folderList.value = getFolderListUseCase(path)
            }
        }

    }

    fun setPath(path: String) {
        _currentPath.value = path
    }


    fun isAccessibleFolder(path: String) = path != INACCESSIBLE_FOLDER

    companion object {

        private val INACCESSIBLE_FOLDER = "${Environment.getExternalStorageDirectory().path}/Android"
        private const val DEFAULT_VALUE = "DEFAULT_VALUE"
    }
}