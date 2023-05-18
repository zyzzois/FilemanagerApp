package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oldFilesHashes")
data class FileModelDb(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val fileHashCode: String,
    val fileName: String,
    val path: String,
    val fileType: String
)
