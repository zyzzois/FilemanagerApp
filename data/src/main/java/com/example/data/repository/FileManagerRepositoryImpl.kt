package com.example.data.repository

import android.os.Environment
import android.util.Log
import com.example.data.mapper.FileToEntityMapper
import com.example.data.utils.extensionIs
import com.example.data.utils.extensionType
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.repository.FileManagerRepository
import java.io.File
import javax.inject.Inject

class FileManagerRepositoryImpl @Inject constructor(
    private val mapper: FileToEntityMapper
): FileManagerRepository {


    override suspend fun getFolderList(path: String): List<FileEntity> {
        val root = File(path)
        val fileList = ArrayList<File>()
        fileList.addAll(findFiles(root))
        return fileList.map {
            mapper.mapFolderToFolderEntity(it)
        }
    }

    override suspend fun getFileListByGroup(path: String, fileGroup: FileGroup): List<FileEntity> {
        val root = File(path)
        val fileList = ArrayList<File>()

        fileList.addAll(findFilesByGroup(root, fileGroup))
        return fileList.map { mapper.mapFolderToFolderEntity(it) }
    }

    override suspend fun getRecentUpdatedFileList(): List<FileEntity> {
        TODO("Not yet implemented")
    }

    override fun shareFile(file: FileEntity) {
        TODO("Not yet implemented")
    }

    override fun editFile(file: FileEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteFile(file: FileEntity) {
        TODO("Not yet implemented")
    }

    private fun findFilesByGroup(file: File, fileGroup: FileGroup): ArrayList<File> {
        val list = ArrayList<File>()
        val files = file.listFiles()
        for (singleFile in files!!) {
            if (isAccessibleFolder(singleFile.path)) {
                if (singleFile.isDirectory && !singleFile.isHidden) {
                    list.addAll(findFilesByGroup(singleFile, fileGroup))
                } else if (!singleFile.isDirectory && !singleFile.isHidden) {
                    when (fileGroup) {
                        is FileGroup.IMAGES -> {
                            if (singleFile.extensionIs(extension[0])
                                || singleFile.extensionIs(extension[1])
                                || singleFile.extensionIs(extension[2])) { list.add(singleFile) }
                        }
                        is FileGroup.DOCUMENTS -> {
                            if (singleFile.extensionIs(extension[6])
                                || singleFile.extensionIs(extension[7])
                                || singleFile.extensionIs(extension[9])) { list.add(singleFile) }
                        }
                        is FileGroup.AUDIO -> {
                            if (singleFile.extensionIs(extension[3])
                                || singleFile.extensionIs(extension[4])) { list.add(singleFile) }
                        }
                        is FileGroup.VIDEOS -> {
                            if (singleFile.extensionIs(extension[5])) list.add(singleFile)
                        }
                        is FileGroup.APK -> {
                            if (singleFile.extensionIs(extension[8])) list.add(singleFile)
                        }
                        is FileGroup.ARCHIVES -> {
                            if (singleFile.extensionIs(extension[10])) list.add(singleFile)
                        }
                        else -> {

                        }
                    }
                }
            }
        }
        return list
    }

    private fun findFiles(file: File): ArrayList<File> {
        val list = ArrayList<File>()
        val files = file.listFiles()
        for (singleFile in files!!)
            if (singleFile.isDirectory && !singleFile.isHidden)
                list.add(singleFile)

        for (singleFile in files) {
            if (singleFile.extensionIs(extension[0]) || singleFile.extensionIs(extension[1]) ||
                singleFile.extensionIs(extension[2]) || singleFile.extensionIs(extension[3]) ||
                singleFile.extensionIs(extension[4]) || singleFile.extensionIs(extension[5]) ||
                singleFile.extensionIs(extension[6]) || singleFile.extensionIs(extension[7]) ||
                singleFile.extensionIs(extension[8])
            ) {
                list.add(singleFile)
            }
        }
        return list
    }

    private fun isAccessibleFolder(folderPath: String) = folderPath != INACCESSIBLE_FOLDER


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

        private val defaultPath = Environment.getExternalStorageDirectory().path

        private val INACCESSIBLE_FOLDER = "$defaultPath/Android"
    }
}