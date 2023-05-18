package com.example.data.database

import androidx.room.*

@Dao
interface FilesHashesDao {
    @Query("SELECT * FROM oldFilesHashes")
    fun getOldFiles(): List<FileModelDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFiles(fileList: List<FileModelDb>)

    @Query("DELETE FROM oldFilesHashes")
    suspend fun clearOldFilesTable()
}