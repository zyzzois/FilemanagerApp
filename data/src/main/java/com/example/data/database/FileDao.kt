package com.example.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FileDao {
    @Query("SELECT * FROM filesHashes")
    fun getRecentUpdatedFileList(): List<FileModelDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFile(fileModelDb: FileModelDb)
}