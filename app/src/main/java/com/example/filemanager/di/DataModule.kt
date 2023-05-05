package com.example.filemanager.di

import com.example.data.repository.FileManagerRepositoryImpl
import com.example.domain.repository.FileManagerRepository
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindFileManagerRepository(impl: FileManagerRepositoryImpl): FileManagerRepository
}