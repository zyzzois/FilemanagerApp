package com.example.filemanager.di

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.database.FileDao
import com.example.data.repository.FileManagerRepositoryImpl
import com.example.domain.repository.FileManagerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindFileManagerRepository(impl: FileManagerRepositoryImpl): FileManagerRepository

    companion object {
        @ApplicationScope
        @Provides
        fun provideFileDao(
            application: Application
        ): FileDao {
            return AppDatabase.getInstance(application).notesDao()
        }
    }
}