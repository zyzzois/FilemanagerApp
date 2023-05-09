package com.example.data.repository

import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.data.mapper.FileToEntityMapper
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.repository.FileManagerRepository
import java.io.File
import javax.inject.Inject

class FileManagerRepositoryImpl @Inject constructor(
    private val mapper: FileToEntityMapper
): FileManagerRepository {


    override suspend fun getFolderList(path: String): List<FileEntity> {
        val rootDir = Environment.getExternalStorageDirectory()
        val list = ArrayList<File>()
        if (path == rootDir.path) {
            val files = rootDir.listFiles()
            for (singleFile in files!!)
                list.add(singleFile)
            return list.map {
                mapper.mapFolderToFolderEntity(it)
            }
        } else {
            val files = File(path).listFiles()
            for (singleFile in files!!)
                list.add(singleFile)
            return list.map {
                mapper.mapFolderToFolderEntity(it)
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
                Log.d("ZYZZ", "true")
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

    override suspend fun getRecentUpdatedFileList(): List<FileEntity> { TODO("Not yet implemented") }

    override fun shareFile(file: FileEntity) { TODO("Not yet implemented") }

    override fun editFile(file: FileEntity) { TODO("Not yet implemented") }

    override fun deleteFile(file: FileEntity) { TODO("Not yet implemented") }


    companion object {
        internal val extension = listOf(
            ".jpeg",
            ".jpg",
            ".png",
            ".mp3",
            ".wav",
            ".mp4",
            ".pdf",
            ".doc",
            ".apk",
            ".docx",
            ".zip"
        )
    }
}