package com.example.filemanager.di

import android.app.Application
import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.database.FilesHashesDao
import com.example.data.database.RecentUpdatedFilesDao
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
        fun provideContext(application: Application): Context {
            return application.applicationContext
        }

        @ApplicationScope
        @Provides
        fun provideFilesHashesDao(
            application: Application
        ): FilesHashesDao {
            return AppDatabase.getInstance(application).filesHashesDao()
        }

        @ApplicationScope
        @Provides
        fun provideRecentUpdatedFiledFao(
            application: Application
        ): RecentUpdatedFilesDao {
            return AppDatabase.getInstance(application).recentUpdatedFiledFao()
        }
    }
}