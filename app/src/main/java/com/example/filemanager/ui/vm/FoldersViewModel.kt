package com.example.filemanager.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.FileEntity
import com.example.domain.usecase.GetFolderListUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class FoldersViewModel @Inject constructor(
    private val getFolderListUseCase: GetFolderListUseCase,
) : ViewModel() {

    private val _currentPath = MutableLiveData<String>()
    val currentPath: LiveData<String>
        get() = _currentPath

    private val _folderList = MutableLiveData<List<FileEntity>?>()
    val folderList: LiveData<List<FileEntity>?>
        get() = _folderList

    fun updateList(path: String) {
        viewModelScope.launch {
            _folderList.postValue(getFolderListUseCase(path))
        }
    }

    fun setPath(path: String) {
        _currentPath.value = path
    }


}