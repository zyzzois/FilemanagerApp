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



}