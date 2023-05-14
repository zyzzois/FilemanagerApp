package com.example.domain.repository

import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup

interface FileManagerRepository {
    suspend fun getFolderList(path: String): List<FileEntity>
    suspend fun getFileListByGroup(fileGroup: FileGroup): List<FileEntity>
    suspend fun getRecentUpdatedFileList(): List<FileEntity>
    suspend fun uploadFilesHashesToDatabase()
    suspend fun clearRecentUpdatedFileList()
    fun shareFile(file: FileEntity)
    fun deleteFile(file: FileEntity)
}