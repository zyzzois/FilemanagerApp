package com.example.domain.entity

import java.io.File

data class FileEntity(
    val filename: String,
    val timestamp: Long,
    val fileType: FileType,
    val file: File,
    val fileSize: Long,
    var folderLength: String = UNDEFINED_LENGTH,
    var isFolder: Boolean = false
) {
    companion object {
        const val UNDEFINED_LENGTH = ""
    }
}
