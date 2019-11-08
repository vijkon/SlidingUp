package com.app.model.main.maps

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList
import java.util.HashMap

@Parcelize
data class PolylineData(val routeInfo : RouteData,
                        val ployLineRoutesList : ArrayList<HashMap<String, String>>) : Parcelable