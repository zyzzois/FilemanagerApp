package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recentUpdatedFiles")
data class RecentUpdatedFileModelDb(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val fileName: String,
    val path: String,
    var fileType: String = "fileType"
)
