package com.example.domain.entity

import java.io.File

data class FileEntity(
    val filename: String,
    val timestamp: String,
    val fileType: FileType,
    val file: File
)
