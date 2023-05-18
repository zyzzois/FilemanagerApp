package com.example.domain.entity

import java.io.File

data class FileEntity(
    val filename: String,
    var timestamp: Long = 0,
    val fileType: FileType,
    val file: File,
    val fileSize: Long,
    var folderLength: String = UNDEFINED_LENGTH,
    var numOfFilesInFolder: Int = 0,
    var id: Int = UNDEFINED_ID,
    var timestampString: String = ""
) {
    companion object {
        const val UNDEFINED_LENGTH = ""
        const val UNDEFINED_ID = 0
    }
}
