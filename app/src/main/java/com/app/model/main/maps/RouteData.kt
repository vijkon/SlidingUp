package com.app.model.main.maps

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteData(val distance : String,
                     val duration : String,
                     val endAddress : String,
                     val startAddress : String) : Parcelable