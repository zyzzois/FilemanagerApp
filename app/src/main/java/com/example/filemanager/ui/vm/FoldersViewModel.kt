package com.example.filemanager.ui.vm

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.FileEntity
import com.example.domain.usecase.DeleteFileUseCase
import com.example.domain.usecase.GetFolderListUseCase
import kotlinx.coroutines.launch
import java.io.File
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

    fun updateList(path: String) {
        viewModelScope.launch {
            _folderList.postValue(getFolderListUseCase(path))
        }
    }

    fun deleteFile(file: FileEntity) {
        deleteFileUseCase(file)
        _folderList.value = folderList.value?.filterNot {
            it == file
        }
    }

    fun setPath(path: String) {
        _currentPath.value = path
    }

    fun isAccessiblePath(path: String) = path != INACCESSIBLE_PATH_1 && path != INACCESSIBLE_PATH_2

    companion object {
        private val INACCESSIBLE_PATH_1 = "${Environment.getExternalStorageDirectory().path}/Android/obb"
        private val INACCESSIBLE_PATH_2 = "${Environment.getExternalStorageDirectory().path}/Android/data"
    }
}