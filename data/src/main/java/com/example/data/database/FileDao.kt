package com.example.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FileDao {
    @Query("SELECT * FROM filesHashes")
    fun getRecentUpdatedFileList(): LiveData<List<FileModelDb>>

    @Insert
    suspend fun insertNewUpdatedFile(file: FileModelDb)
}