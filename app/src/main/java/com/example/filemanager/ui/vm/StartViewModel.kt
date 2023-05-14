package com.example.filemanager.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.ClearRecentUpdatedFileListUseCase
import com.example.domain.usecase.UploadFilesHashesToDatabaseUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val uploadFilesHashesToDatabaseUseCase: UploadFilesHashesToDatabaseUseCase,
    private val clearRecentUpdatedFileListUseCase: ClearRecentUpdatedFileListUseCase
): ViewModel() {

    fun uploadFilesHashesToDatabase() {
        viewModelScope.launch {
            uploadFilesHashesToDatabaseUseCase()
        }
    }

    fun clearDatabase() {
        viewModelScope.launch() {
            clearRecentUpdatedFileListUseCase()
        }
    }

}