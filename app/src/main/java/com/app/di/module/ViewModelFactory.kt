package com.app.di.module

import androidx.lifecycle.ViewModelProvider
import com.app.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactory {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory) : ViewModelProvider.Factory
}
