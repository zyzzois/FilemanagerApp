package com.example.filemanager.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.UploadFilesHashesToDatabaseUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val uploadFilesHashesToDatabaseUseCase: UploadFilesHashesToDatabaseUseCase
): ViewModel() {

    private val _lastRunTime = MutableLiveData<Long>()

    fun uploadRecentUpdatedFilesToDatabase() {
        viewModelScope.launch {
            uploadFilesHashesToDatabaseUseCase()
        }
    }

    fun setLastRunTime(lastRunTime: Long) {
        _lastRunTime.value = lastRunTime
    }

}