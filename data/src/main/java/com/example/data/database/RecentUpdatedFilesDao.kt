package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentUpdatedFilesDao {

    @Query("SELECT * FROM recentUpdatedFiles")
    fun getRecentUpdatedFileList(): List<RecentUpdatedFileModelDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFiles(fileList: List<RecentUpdatedFileModelDb>)

    @Query("DELETE FROM recentUpdatedFiles")
    suspend fun clearRecentUpdatedFiles()
}