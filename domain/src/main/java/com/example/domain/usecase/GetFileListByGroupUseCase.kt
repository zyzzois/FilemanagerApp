package com.example.domain.usecase

import androidx.lifecycle.LiveData
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.repository.FileManagerRepository
import javax.inject.Inject

class GetFileListByGroupUseCase @Inject constructor(private val repository: FileManagerRepository) {
    suspend operator fun invoke(path: String, fileGroup: FileGroup): List<FileEntity> {
        return repository.getFileListByGroup(path, fileGroup)
    }
}