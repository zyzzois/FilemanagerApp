package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup

interface FileManagerRepository {
    suspend fun getFolderList(path: String): List<FileEntity>
    suspend fun getFileListByGroup(path: String, fileGroup: FileGroup): List<FileEntity>
    suspend fun getRecentUpdatedFileList(): List<FileEntity>
    fun shareFile(file: FileEntity)
    fun editFile(file: FileEntity)
    fun deleteFile(file: FileEntity)
}