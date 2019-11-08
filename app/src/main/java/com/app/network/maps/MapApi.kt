package com.app.network.maps

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

interface MapApi
{
    @GET
    fun getPolylineDataAsync(@Url url : String): Deferred<JsonObject>
}
