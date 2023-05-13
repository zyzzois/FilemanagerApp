package com.example.data.mapper

import com.example.data.database.FileModelDb
import com.example.data.utils.extensionType
import com.example.domain.entity.FileEntity
import java.io.File
import javax.inject.Inject

class FileToEntityMapper @Inject constructor() {

    fun mapFileToFileEntity(file: File) = FileEntity(
        filename = file.name,
        timestamp = file.lastModified(),
        fileType = file.extensionType(),
        file = file,
        fileSize = file.length(),
        numOfFilesInFolder = countFiles(file)
    )


    fun mapDBModelToEntity(fileDb: FileModelDb) = FileEntity(
        filename = fileDb.fileName,
        fileType = File(fileDb.path).extensionType(),
        file = File(fileDb.path),
        fileSize = 0
    )

    fun mapEntityToDbModel(fileEntity: FileEntity) = FileModelDb(
        id = fileEntity.id,
        fileHashCode = fileEntity.hashCode(),
        fileName = fileEntity.filename,
        path = fileEntity.file.path,
        fileType = fileEntity.file.extension
    )

    private fun countFiles(file: File) = if (file.isFile) 0 else file.listFiles()?.size ?: 0
}