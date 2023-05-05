package com.example.domain.usecase

import com.example.domain.entity.FileEntity
import com.example.domain.repository.FileManagerRepository
import javax.inject.Inject

class DeleteFileUseCase @Inject constructor(private val repository: FileManagerRepository) {
    operator fun invoke(file: FileEntity) {
        repository.deleteFile(file)
    }
}