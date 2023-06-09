package com.example.filemanager.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.FileEntity
import com.example.domain.usecase.GetRecentUpdatedFileListUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getRecentUpdatedFileListUseCase: GetRecentUpdatedFileListUseCase
): ViewModel() {
    private val _lastModifiedFileList = MutableLiveData<List<FileEntity>?>()
    val lastModifiedFileList: LiveData<List<FileEntity>?>
        get() = _lastModifiedFileList

    fun findLastModifiedFileList() {
        viewModelScope.launch {
            _lastModifiedFileList.postValue(getRecentUpdatedFileListUseCase())
        }
    }

}