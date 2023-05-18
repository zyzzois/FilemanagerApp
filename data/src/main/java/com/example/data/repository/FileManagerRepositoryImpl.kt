package com.example.data.repository

import android.os.Environment
import android.util.Log
import com.example.data.database.FileModelDb
import com.example.data.database.FilesHashesDao
import com.example.data.database.RecentUpdatedFilesDao
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
    private val filesHashesDao: FilesHashesDao,
    private val recentUpdatedFilesDao: RecentUpdatedFilesDao
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

//    старый метод
//    override suspend fun getRecentUpdatedFileList(): List<FileEntity> {
//        return withContext(Dispatchers.IO) {
//            val recentUpdatedFiles = mutableListOf<FileEntity>()
//            val filesFromDb = recentUpdatedFilesDao.getRecentUpdatedFileList()
//            filesFromDb.forEach {
//                recentUpdatedFiles.add(mapper.mapRecentUpdatedFileModelDbToEntity(it))
//            }
//            recentUpdatedFiles
//        }
//    }

    override suspend fun getRecentUpdatedFileList(): List<FileEntity> {
        return withContext(Dispatchers.IO) {
            val recentUpdatedFiles = mutableListOf<FileEntity>()
            val defaultPath = Environment.getExternalStorageDirectory()
            val files = getAllFiles(defaultPath)
            Log.d("RECENT", files.toString())

            files.forEach {
                if (it.isFile && extension.contains(it.extension) && it.canRead()) {
                    recentUpdatedFiles.add(mapper.mapFileToFileEntity(it))
                }
            }
            recentUpdatedFiles.sortByDescending {
                it.file.lastModified()
            }
            Log.d("RECENT", recentUpdatedFiles.toString())

            recentUpdatedFiles
        }
    }

    override suspend fun uploadRecentUpdatesFilesToDatabase(lastRunTime: Long) = withContext(Dispatchers.IO) {
        val recentUpdatedFiles = mutableListOf<FileEntity>()
        val defaultPath = Environment.getExternalStorageDirectory()
        val files = getAllFiles(defaultPath)


        files.forEach {
            if (it.isFile && extension.contains(it.extension) && it.canRead()) {
                recentUpdatedFiles.add(mapper.mapFileToFileEntity(it))
            }
        }
        recentUpdatedFiles.sortByDescending {
            it.file.lastModified()
        }
        //while ()
    }


//    старый метод
//    override suspend fun uploadRecentUpdatesFilesToDatabase(lastRunTime: Long) = withContext(Dispatchers.IO) {
//        val recentUpdatedFiles = mutableListOf<RecentUpdatedFileModelDb>()
//        val oldFiles = filesHashesDao.getOldFiles()
//        for (file in oldFiles) {
//            val currentFile = File(file.path)
//            if (currentFile.exists()) {
//                if (currentFile.lastModified() > lastRunTime) {
//                    val currentFileHashCode = mapper.getFileHashCode(currentFile)
//                    if (currentFileHashCode != file.fileHashCode)
//                        recentUpdatedFiles.add(
//                            mapper.mapFileModelDbToRecentUpdatedFileModelDb(file)
//                        )
//                }
//            }
//        }
//        recentUpdatedFilesDao.addFiles(recentUpdatedFiles)
//    }

    override suspend fun uploadFilesHashesToDatabase() = withContext(Dispatchers.IO) {
        filesHashesDao.clearOldFilesTable()
        Log.d("FileManagerRepositoryImpl", "Зашли в метод uploadFilesHashesToDatabase")
        val fileList = mutableListOf<FileModelDb>()
        val rootFile = Environment.getExternalStorageDirectory()
        val files = getAllFiles(rootFile)
        Log.d("FileManagerRepositoryImpl", "Получили все файлы")
        files.forEach {
            if (it.isFile && it.canRead() && extension.contains(it.extension)) {
                fileList.add(mapper.mapEntityToDbModel(mapper.mapFileToFileEntity(it)))
            }
        }
        Log.d("FileManagerRepositoryImpl", "Получили список всех хешкодов")
        filesHashesDao.addFiles(fileList)
    }

    override suspend fun clearRecentUpdatedFileList() {
        recentUpdatedFilesDao.clearRecentUpdatedFiles()
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

    companion object {
        private const val DEFAULT_VALUE = "DEFAULT_VALUE"
        internal val extension = listOf(
            "jpeg",
            "jpg",
            "png",
            "mp3",
            "wav",
            "mp4",
            "pdf",
            "doc",
            "apk",
            "zip",
            "xlsx",
            "ppt"
        )
    }
}