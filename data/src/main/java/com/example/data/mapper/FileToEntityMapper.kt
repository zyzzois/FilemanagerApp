package com.example.data.mapper

import com.example.data.utils.extensionType
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileType
import java.io.File
import javax.inject.Inject

class FileToEntityMapper @Inject constructor() {

    fun mapFolderToFolderEntity(file: File) = FileEntity(
        filename = file.name,
        timestamp = file.name,
        fileType = file.extensionType(),
        file = file
    )

    fun mapFolderEntityToFolder(folderEntityList: List<FileEntity>): ArrayList<File> {
        val res = ArrayList<File>()
        folderEntityList.forEach {
            res.add(mapFileEntityToFile(it))
        }
        return res
    }

    private fun mapFileEntityToFile(fileEntity: FileEntity) = File(fileEntity.file.path)

}