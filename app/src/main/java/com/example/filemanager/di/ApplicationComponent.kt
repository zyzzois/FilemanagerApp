package com.example.filemanager.di

import android.app.Application
import com.example.filemanager.ui.screens.AuthorizationFragment
import com.example.filemanager.ui.screens.FilesByTypeFragment
import com.example.filemanager.ui.screens.FoldersFragment
import com.example.filemanager.ui.screens.HomeFragment
import com.example.filemanager.ui.screens.MainActivity
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        ViewModelModule::class,
        DataModule::class
    ]
)
interface ApplicationComponent {
    fun inject(fragment: FoldersFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: FilesByTypeFragment)
    fun inject(activity: MainActivity)
    fun inject(fragment: AuthorizationFragment)

    @Component.Factory
    interface ApplicationComponentFactory {
        fun create(
            @BindsInstance application: Application,
        ): ApplicationComponent
    }
}