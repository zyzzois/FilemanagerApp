package com.example.domain.usecase

import com.example.domain.entity.FileEntity
import com.example.domain.repository.FileManagerRepository
import java.io.File
import javax.inject.Inject

class GetFolderListUseCase @Inject constructor(private val repository: FileManagerRepository) {
    suspend operator fun invoke(path: String): List<FileEntity> {
        return repository.getFolderList(path)
    }
}