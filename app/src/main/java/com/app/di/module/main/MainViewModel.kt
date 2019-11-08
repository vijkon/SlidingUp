package com.app.di.module.main

import androidx.lifecycle.ViewModel
import com.app.di.annotations.GeneratedKey
import com.app.ui.main.MainViewModel
import com.app.ui.main.maps.PolyLineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModel
{
    @Binds
    @IntoMap
    @GeneratedKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @GeneratedKey(PolyLineViewModel::class)
    abstract fun bindPolylineViewModel(viewModel: PolyLineViewModel): ViewModel
}