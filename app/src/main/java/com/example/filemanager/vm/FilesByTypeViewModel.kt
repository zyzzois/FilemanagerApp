package com.example.filemanager.vm

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.entity.FileType
import com.example.domain.usecase.GetFileListByGroupUseCase
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class FilesByTypeViewModel @Inject constructor(
    private val getFileListByGroupUseCase: GetFileListByGroupUseCase
): ViewModel() {

    private val defaultPath = Environment.getExternalStorageDirectory().path

    private val _fileList = MutableLiveData<List<FileEntity>?>()
    val fileList: LiveData<List<FileEntity>?>
        get() = _fileList

    fun updateList(fileGroup: FileGroup) {
        when (fileGroup) {
            is FileGroup.DOWNLOADS -> {
                val path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ).path
                viewModelScope.launch {
                    _fileList.value = getFileListByGroupUseCase(path, fileGroup)
                }

            }
            is FileGroup.DOCUMENTS -> {
                val path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS
                ).path
                viewModelScope.launch {
                    _fileList.value = getFileListByGroupUseCase(path, fileGroup)
                }
            }

            else -> {
                viewModelScope.launch {
                    _fileList.value = getFileListByGroupUseCase(defaultPath, fileGroup)
                }
            }
        }
        viewModelScope.launch {
            _fileList.value = getFileListByGroupUseCase(defaultPath, fileGroup)
        }

    }


}