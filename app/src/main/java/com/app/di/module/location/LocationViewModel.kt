package com.app.di.module.location

import androidx.lifecycle.ViewModel
import com.app.di.annotations.GeneratedKey
import com.app.location.LocationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LocationViewModel
{
    @Binds
    @IntoMap
    @GeneratedKey(LocationViewModel::class)
    abstract fun bindLocationViewModel(viewModel: LocationViewModel): ViewModel
}