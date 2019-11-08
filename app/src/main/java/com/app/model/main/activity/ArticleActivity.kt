package com.app.model.main.activity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArticleActivity(@SerializedName("geosearch") val geoSearchList : List<FetchGeoLocationList>) : Parcelable