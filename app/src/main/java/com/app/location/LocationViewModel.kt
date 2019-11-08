package com.app.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.extensionFunction.NonNullMediatorLiveData
import com.app.util.UiHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import javax.inject.Inject

class LocationViewModel @Inject constructor(private val locationProviderClient: FusedLocationProviderClient) : ViewModel()
{
    private val _currentLocation = NonNullMediatorLiveData<Location>()
    val currentLocation : LiveData<Location> = _currentLocation
    @Inject lateinit var uiHelper: UiHelper

    /**
     * @return the [LocationResult] with include the Location Lat,Lng
     */
    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.lastLocation.apply {
                _currentLocation.postValue(this)
            }
        }
    }

    /**
     * This function is set to update the user location in a given time of interval.
     */

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        locationProviderClient.requestLocationUpdates(uiHelper.getLocationRequest(), locationCallback, Looper.myLooper())
    }

    /**
     * This function is set to stop the location update
     */

    fun stopLocationUpdates() {
        locationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}