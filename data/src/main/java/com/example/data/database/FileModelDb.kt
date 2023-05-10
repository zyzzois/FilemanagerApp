package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.entity.FileType
import java.io.File

@Entity(tableName = "filesHashes")
data class FileModelDb(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val fileHashCode: Int
)
