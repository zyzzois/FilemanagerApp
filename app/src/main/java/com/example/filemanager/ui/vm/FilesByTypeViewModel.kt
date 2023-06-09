package com.example.filemanager.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.usecase.GetFileListByGroupUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilesByTypeViewModel @Inject constructor(
    private val getFileListByGroupUseCase: GetFileListByGroupUseCase
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

    fun showFilesInSelectedGroup(fileGroup: FileGroup) {
        viewModelScope.launch {
            _fileList.postValue(getFileListByGroupUseCase(fileGroup))
        }
    }




    fun setPath(path: String) {
        _currentPath.value = path
    }

}