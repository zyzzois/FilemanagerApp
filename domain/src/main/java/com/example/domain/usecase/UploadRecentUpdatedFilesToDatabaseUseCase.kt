package com.example.domain.usecase

import com.example.domain.repository.FileManagerRepository
import javax.inject.Inject

class UploadRecentUpdatedFilesToDatabaseUseCase @Inject constructor(
    private val repository: FileManagerRepository
) {
    suspend operator fun invoke(lastRunTime: Long) {
        repository.uploadRecentUpdatesFilesToDatabase(lastRunTime)
    }
}