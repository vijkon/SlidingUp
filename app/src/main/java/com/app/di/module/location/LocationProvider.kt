package com.app.di.module.location

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
class LocationProvider
{
    @Module
    companion object
    {
        @Provides
        @JvmStatic
        fun getLocation(application: Application): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(application)
        }
    }
}