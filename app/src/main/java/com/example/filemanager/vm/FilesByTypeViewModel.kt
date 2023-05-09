package com.example.filemanager.vm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.usecase.GetFileListByGroupUseCase
import com.example.domain.usecase.GetFolderListUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilesByTypeViewModel @Inject constructor(
    private val getFileListByGroupUseCase: GetFileListByGroupUseCase,
    private val getFolderListUseCase: GetFolderListUseCase
): ViewModel() {

    private val _fileList = MutableLiveData<List<FileEntity>?>()
    val fileList: LiveData<List<FileEntity>?>
        get() = _fileList

    private val _currentPath = MutableLiveData<String>()
    val currentPath: LiveData<String>
        get() = _currentPath

    private val _shouldOpenFilesInFolder = MutableLiveData<Boolean>()
    val shouldOpenFilesInFolder: LiveData<Boolean>
        get() = _shouldOpenFilesInFolder



    fun showFoldersInFileGroup(fileGroup: FileGroup) {
        viewModelScope.launch {
            _fileList.value = getFileListByGroupUseCase(fileGroup)
            Log.d("ZYZZ", "${_fileList.value}")
        }
    }

    fun showFilesInSelectedFolder(path: String?) {
        viewModelScope.launch {
            if (path == null || path == DEFAULT_PATH) {
                Log.d("ZYZZ", "showFilesInSelectedFolder path == null || path == DEFAULT_PATH")
            }
            else {
                viewModelScope.launch {
                    _fileList.value = getFolderListUseCase(path)
                }
            }
        }
    }

    fun setPath(path: String) {
        _currentPath.value = path
    }

    fun validatePath(argsPath: String?) {
        if (argsPath == DEFAULT_PATH || argsPath == null) {
            _shouldOpenFilesInFolder.value = true
        } else {
            _shouldOpenFilesInFolder.value = true
        }
    }


    companion object {
        private const val DEFAULT_PATH = "DEFAULT_PATH"
    }
}