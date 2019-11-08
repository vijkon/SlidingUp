package com.app.network.main

import com.app.model.main.activity.NearByGeoArticle
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface
{
    @GET("api.php")
    fun getNearbyArticles(@Query("action") action: String, @Query("list") list: String,
                          @Query("gsradius") gsRadius: Long, @Query("gscoord") coordinates: String,
                          @Query("gslimit") gslimit: Int, @Query("format") format: String)
            : Deferred<NearByGeoArticle>

    @GET("api.php")
    fun getArticleInfo(@Query("action") action: String, @Query("prop") prop : String,
                       @Query("pageids") pageId : Long, @Query("format") format: String)
            : Deferred<JsonObject>
}