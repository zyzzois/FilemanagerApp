package com.example.data.repository

import android.os.Environment
import android.util.Log
import com.example.data.mapper.FileToEntityMapper
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.repository.FileManagerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FileManagerRepositoryImpl @Inject constructor(
    private val mapper: FileToEntityMapper
): FileManagerRepository {

    override suspend fun getFolderList(path: String): List<FileEntity> {
        return withContext(Dispatchers.IO) {
            val rootDir = Environment.getExternalStorageDirectory()
            val rootDirPath = rootDir.path
            val list = ArrayList<File>()
            val resultList = ArrayList<FileEntity>()
            var actualPath = path

            if (path == DEFAULT_VALUE)
                actualPath = rootDirPath

            if (actualPath == rootDirPath) {
                val files = rootDir.listFiles()
                for (singleFile in files!!)
                    list.add(singleFile)
                list.forEach {
                    resultList.add(mapper.mapFolderToFolderEntity(it))
                }
                resultList
            }

            else {
                val files = File(path).listFiles()
                for (singleFile in files!!)
                    list.add(singleFile)

                list.forEach {
                    resultList.add(mapper.mapFolderToFolderEntity(it))
                }
                resultList
            }
        }
    }

    override suspend fun getFileListByGroup(fileGroup: FileGroup): List<FileEntity> {
        val paths = mutableListOf<String>()
        when (fileGroup) {
            is FileGroup.AUDIO -> {
                paths.add(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC).path)
                paths.add(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_ALARMS).path)
                paths.add(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_RINGTONES).path)
            }

            is FileGroup.DOCUMENTS -> paths.add(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS).path)

            is FileGroup.VIDEOS -> paths.add(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES).path)

            is FileGroup.DOWNLOADS -> paths.add(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).path)

            is FileGroup.IMAGES -> {
                paths.add(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).path)
                paths.add(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).path)
            }

            else -> {
                paths.add(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).path)
            }
        }
        val fileList = ArrayList<File>()
        for (path in paths) {
            val currentFile = File(path)
            val files = currentFile.listFiles()
            for (file in files!!)
                fileList.add(file)
        }
        return fileList.map { mapper.mapFolderToFolderEntity(it) }
    }

    override fun deleteFile(file: FileEntity) {
        val fileToDelete = file.file
        if (fileToDelete.exists())
            fileToDelete.delete()
    }

    override suspend fun getRecentUpdatedFileList(): List<FileEntity> {
        return withContext(Dispatchers.IO) {
            val rootDir = Environment.getExternalStorageDirectory()
            val list = ArrayList<File>()
            val resultList = ArrayList<FileEntity>()
            val files = rootDir.listFiles()
            for (singleFile in files!!) {
                list.add(singleFile)

            }


            list.forEach {
                resultList.add(mapper.mapFolderToFolderEntity(it))
            }


            val res = resultList.sortedByDescending {
                it.file.lastModified()
            }
            res
        }
    }

    override fun editFile(file: FileEntity) {

    }

    override fun shareFile(file: FileEntity) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val DEFAULT_VALUE = "DEFAULT_VALUE"
        internal val extension = listOf(
            ".jpeg", ".jpg", ".png", ".mp3",
            ".wav", ".mp4", ".pdf", ".doc",
            ".apk", ".docx", ".zip"
        )
    }
}