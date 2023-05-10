package com.example.domain.usecase

import com.example.domain.entity.FileEntity
import com.example.domain.repository.FileManagerRepository
import javax.inject.Inject

class GetRecentUpdatedFileListUseCase @Inject constructor(
    private val repository: FileManagerRepository
) {
    suspend operator fun invoke(): List<FileEntity> {
        return repository.getRecentUpdatedFileList()
    }
}