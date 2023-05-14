package com.example.filemanager.ui.vm

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.FileEntity
import com.example.domain.usecase.DeleteFileUseCase
import com.example.domain.usecase.GetFolderListUseCase
import com.example.filemanager.utils.Constants.DEFAULT_VALUE
import kotlinx.coroutines.launch
import javax.inject.Inject

class FoldersViewModel @Inject constructor(
    private val getFolderListUseCase: GetFolderListUseCase,
    private val deleteFileUseCase: DeleteFileUseCase
) : ViewModel() {

    private val _currentPath = MutableLiveData<String>()
    val currentPath: LiveData<String>
        get() = _currentPath

    private val _folderList = MutableLiveData<List<FileEntity>?>()
    val folderList: LiveData<List<FileEntity>?>
        get() = _folderList

    private val _searchedQueryList = MutableLiveData<List<FileEntity>>()
    val searchedQueryList: LiveData<List<FileEntity>>
        get() = _searchedQueryList

    fun updateList(path: String) {
        viewModelScope.launch {
            _folderList.postValue(getFolderListUseCase(path))
        }
        if (path == DEFAULT_VALUE)
            _currentPath.value = Environment.getExternalStorageDirectory().path
        else
            _currentPath.value = path
    }

    fun deleteFile(file: FileEntity) {
        deleteFileUseCase(file)
        _folderList.value = folderList.value?.filterNot {
            it == file
        }
    }

    fun searchInCurrentList(query: String) {
        val list = _folderList.value ?: return
        val filteredList = list.filter { it.filename.lowercase().contains(query.lowercase()) }
        _searchedQueryList.value = filteredList
    }
}