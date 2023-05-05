package com.example.filemanager.di

import androidx.lifecycle.ViewModel
import com.example.filemanager.vm.FilesByTypeViewModel
import com.example.filemanager.vm.FoldersViewModel
import com.example.filemanager.vm.HomeViewModel
import com.example.filemanager.vm.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @Binds
    @ViewModelKey(FoldersViewModel::class)
    fun bindFoldersViewModel(viewModel: FoldersViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(FilesByTypeViewModel::class)
    fun bindFilesByTypeViewModel(viewModel: FilesByTypeViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel

}