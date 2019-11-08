package com.app.model.main.activity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FetchGeoLocationList(@SerializedName("pageid") val pageId : Long,
                                @SerializedName("title") val title : String,
                                @SerializedName("lat") val latitude : Double,
                                @SerializedName("lon") val longitude : Double) : Parcelable