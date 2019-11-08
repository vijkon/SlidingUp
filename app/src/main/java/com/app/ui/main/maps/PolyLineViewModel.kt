package com.app.ui.main.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.extensionFunction.NonNullMediatorLiveData
import com.app.model.main.maps.PolylineData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PolyLineViewModel @Inject constructor(private val googleMapRepository : PolyLineRepository) : ViewModel()
{
    // FOR DATA ---
    private val lists = NonNullMediatorLiveData<List<PolylineData>>()
    val polyLineList : LiveData<List<PolylineData>> = lists

    /**
     * Fetch a list of LatLng and Route Info by PolylineData
     */

    fun getPolyline(markerLatLng : LatLng,currentLatLng : LatLng)
    {
        CoroutineScope(Dispatchers.IO).launch {
            googleMapRepository.getPolyLineData(lists,markerLatLng,currentLatLng)
        }
    }
}