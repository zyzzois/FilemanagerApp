package com.example.filemanager.di

import androidx.lifecycle.ViewModel
import com.example.filemanager.ui.vm.FilesByTypeViewModel
import com.example.filemanager.ui.vm.FoldersViewModel
import com.example.filemanager.ui.vm.HomeViewModel
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
    @ViewModelKey(FilesByTypeViewModel::class)
    fun bindFilesByTypeViewModel(viewModel: FilesByTypeViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel


}