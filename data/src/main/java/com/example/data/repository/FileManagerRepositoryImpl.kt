package com.example.data.repository

import android.os.Environment
import com.example.data.database.FileDao
import com.example.data.mapper.FileToEntityMapper
import com.example.data.utils.extensionIs
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileGroup
import com.example.domain.repository.FileManagerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FileManagerRepositoryImpl @Inject constructor(
    private val mapper: FileToEntityMapper,
    private val fileDao: FileDao
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
                    resultList.add(mapper.mapFileToFileEntity(it))
                }
                resultList
            }

            else {
                val files = File(path).listFiles()
                for (singleFile in files!!)
                    list.add(singleFile)

                list.forEach {
                    resultList.add(mapper.mapFileToFileEntity(it))
                }
                resultList
            }
        }
    }

    override suspend fun getFileListByGroup(fileGroup: FileGroup): List<FileEntity> {
        return withContext(Dispatchers.IO) {
            val filesByGroup = getAllFilesByGroup(
                File(Environment.getExternalStorageDirectory().path),
                fileGroup
            )
            filesByGroup.map {
                mapper.mapFileToFileEntity(it)
            }
        }
    }

    override fun deleteFile(file: FileEntity) {
        val fileToDelete = file.file
        if (fileToDelete.exists())
            fileToDelete.delete()
    }

    override suspend fun getRecentUpdatedFileList(): List<FileEntity> {
        return withContext(Dispatchers.IO) {
            val recentUpdatedFiles = mutableListOf<FileEntity>()
            val filesFromDb = fileDao.getRecentUpdatedFileList()
            for (file in filesFromDb)
                if (File(file.path).hashCode() != file.fileHashCode)
                    recentUpdatedFiles.add(mapper.mapDBModelToEntity(file))
            recentUpdatedFiles
        }
    }

    override suspend fun uploadFilesHashesToDatabase() {
        val files = getAllFiles(Environment.getExternalStorageDirectory())
        files.forEach {
            fileDao.addFile(mapper.mapEntityToDbModel(mapper.mapFileToFileEntity(it)))
        }
    }

    override suspend fun clearRecentUpdatedFileList() {
        fileDao.clearRecentUpdatedFiles()
    }

    private suspend fun getAllFiles(dir: File): List<File> {
        return withContext(Dispatchers.IO) {
            val files = mutableListOf<File>()
            val listFiles = dir.listFiles()
            if (listFiles != null) {
                for (file in listFiles) {
                    if (file.isDirectory && file.canRead()) files.addAll(getAllFiles(file))
                    else files.add(file)
                }
            }
            files
        }
    }

    private suspend fun getAllFilesByGroup(dir: File, fileGroup: FileGroup): List<File> {
        return withContext(Dispatchers.IO) {
            val files = mutableListOf<File>()
            if (fileGroup != FileGroup.DOWNLOADS) {
                val listFiles = dir.listFiles()
                if (listFiles != null) {
                    for (file in listFiles) {
                        if (file.isDirectory && file.canRead())
                            files.addAll(getAllFilesByGroup(file, fileGroup))
                        else if (file.isFile)
                            when (fileGroup) {
                                FileGroup.IMAGES -> if (file.extensionIs(extension[0])
                                        || file.extensionIs(extension[1])
                                        || file.extensionIs(extension[2])) files.add(file)

                                FileGroup.VIDEOS -> if (file.extensionIs(extension[5]))
                                    files.add(file)

                                FileGroup.ARCHIVES -> if (file.extensionIs(extension[9]))
                                    files.add(file)

                                FileGroup.DOCUMENTS -> if (file.extensionIs(extension[6])
                                        || file.extensionIs(extension[7])
                                        || file.extensionIs(extension[10])
                                    ) files.add(file)

                                FileGroup.AUDIO -> if (file.extensionIs(extension[3]))
                                    files.add(file)

                                FileGroup.APK -> if (file.extensionIs(extension[8]))
                                        files.add(file)

                                else -> {}
                            }
                    }
                }
            } else {
                val downloadedDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ).listFiles()
                for (file in downloadedDir!!) files.add(file)
            }
            files
        }
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
            ".zip",
            ".xlsx",
            ".ppt"
        )
    }
}