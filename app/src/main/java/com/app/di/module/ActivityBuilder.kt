package com.app.di.module

import com.app.di.module.location.LocationProvider
import com.app.di.module.location.LocationViewModel
import com.app.di.module.main.MainFragment
import com.app.di.module.main.MainModule
import com.app.di.module.main.MainScope
import com.app.di.module.main.MainViewModel
import com.app.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBuilder {

    @MainScope
    @ContributesAndroidInjector(modules = [MainViewModel::class, MainModule::class,
        LocationViewModel::class, LocationProvider::class, MainFragment::class])
    abstract fun getMainActivity(): MainActivity
}