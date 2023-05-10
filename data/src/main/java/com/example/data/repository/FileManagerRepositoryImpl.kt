package com.example.data.repository

import android.os.Environment
import android.util.Log
import com.example.data.mapper.FileToEntityMapper
import com.example.data.utils.extensionType
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.entity.FileType
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
        return withContext(Dispatchers.IO) {
            val path = Environment.getExternalStorageDirectory().path
            val currentFile = File(path)
            val fileList = ArrayList<File>()
            val files = currentFile.listFiles()

            when (fileGroup) {
                FileGroup.AUDIO -> {
                    for (file in files!!)
                        if (file.extensionType() == FileType.MP3
                            || file.extensionType() == FileType.WAV
                        ) fileList.add(file)
                }

                FileGroup.DOCUMENTS -> {
                    for (file in files!!)
                        if (file.extensionType() == FileType.PDF
                            || file.extensionType() == FileType.DOC
                        ) fileList.add(file)
                }

                FileGroup.VIDEOS -> {
                    for (file in files!!)
                        if (file.extensionType() == FileType.MP4) fileList.add(file)
                }

                is FileGroup.DOWNLOADS -> {
                    val actualPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    val list = actualPath.listFiles()
                    for (file in list!!)
                        fileList.add(file)
                }

                is FileGroup.IMAGES -> {
                    for (file in files!!)
                        if (file.extensionType() == FileType.PNG
                            || file.extensionType() == FileType.JPEG
                            || file.extensionType() == FileType.JPG
                        ) fileList.add(file)
                }
                is FileGroup.APK -> {
                    for (file in files!!)
                        if (file.extensionType() == FileType.APK)
                            fileList.add(file)
                }

                is FileGroup.ARCHIVES -> {
                    for (file in files!!)
                        if (file.extensionType() == FileType.ZIP)
                            fileList.add(file)
                }

                else -> {
                    val actualPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    val list = actualPath.listFiles()
                    for (file in list!!)
                        fileList.add(file)
                }
            }
            val finalList = fileList.map { mapper.mapFolderToFolderEntity(it) }
            Log.d("ANUBIS", finalList.toString())
            finalList
        }
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
            ".jpeg",
            ".jpg",
            ".png",
            ".mp3",
            ".wav",
            ".mp4",
            ".pdf",
            ".doc",
            ".apk",
            ".zip"
        )
    }
}