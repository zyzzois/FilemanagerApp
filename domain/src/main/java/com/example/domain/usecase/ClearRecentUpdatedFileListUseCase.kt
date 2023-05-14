package com.example.domain.usecase

import com.example.domain.repository.FileManagerRepository
import javax.inject.Inject

class ClearRecentUpdatedFileListUseCase @Inject constructor(private val repository: FileManagerRepository) {
    suspend operator fun invoke() {
        repository.clearRecentUpdatedFileList()
    }
}