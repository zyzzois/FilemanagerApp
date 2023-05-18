package com.example.data.mapper

import com.example.data.database.FileModelDb
import com.example.data.database.RecentUpdatedFileModelDb
import com.example.data.utils.extensionType
import com.example.domain.entity.FileEntity
import com.google.common.hash.Hashing
import com.google.common.io.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class FileToEntityMapper @Inject constructor() {

    fun mapFileToFileEntity(file: File) = FileEntity(
        filename = file.name,
        timestamp = file.lastModified(),
        fileType = file.extensionType(),
        file = file,
        fileSize = file.length(),
        numOfFilesInFolder = countFiles(file),
        timestampString = formatMillis(file.lastModified())
    )

    suspend fun mapEntityToDbModel(fileEntity: FileEntity) = FileModelDb(
        id = fileEntity.id,
        fileHashCode = getFileHashCode(fileEntity.file),
        fileName = fileEntity.filename,
        path = fileEntity.file.path,
        fileType = fileEntity.file.extension
    )

    fun mapFileModelDbToRecentUpdatedFileModelDb(fileModelDb: FileModelDb) = RecentUpdatedFileModelDb(
        id = fileModelDb.id,
        fileName = fileModelDb.fileName,
        path = fileModelDb.path,
        fileType = fileModelDb.fileType
    )

    fun mapRecentUpdatedFileModelDbToEntity(fileModelDb: RecentUpdatedFileModelDb) = FileEntity(
        filename = fileModelDb.fileName,
        fileType = File(fileModelDb.path).extensionType(),
        file = File(fileModelDb.path),
        fileSize = File(fileModelDb.path).length()
    )

    private fun formatMillis(millis: Long): String {
        val sdf = SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH)
        return sdf.format(millis)
    }

    private fun countFiles(file: File) = if (file.isFile) 0 else file.listFiles()?.size ?: 0

    suspend fun getFileHashCode(file: File): String = withContext(Dispatchers.IO) {
        val byteSource = Files.asByteSource(file)
        val hc = byteSource.hash(Hashing.adler32())
        val checksum = hc.toString()
        checksum
    }

    companion object {
        private const val DATE_PATTERN = "dd.MM.yyyy HH:mm"
    }
}