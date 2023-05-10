package com.example.data.mapper

import com.example.data.database.FileModelDb
import com.example.data.utils.extensionType
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileType
import java.io.File
import javax.inject.Inject

class FileToEntityMapper @Inject constructor() {

    fun mapFolderToFolderEntity(file: File) = FileEntity(
        filename = file.name,
        timestamp = file.lastModified(),
        fileType = file.extensionType(),
        file = file,
        fileSize = file.length(),
        numOfFilesInFolder = countFiles(file)
    )

    fun mapEntityToDbModel(fileEntity: FileEntity) = FileModelDb(
        fileHashCode = fileEntity.hashCode(),
        id = fileEntity.id
    )

    private fun countFiles(file: File) = if (file.isFile) 0 else file.listFiles()?.size ?: 0
}