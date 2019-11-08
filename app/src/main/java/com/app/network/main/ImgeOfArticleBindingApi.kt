package com.app.network.main

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ImgeOfArticleBindingApi {

    @GET("api.php")
    fun getImageOfArticle(@Query("action") action: String, @Query("format") format: String
                          , @Query("formatversion") formatVersion: Int, @Query("prop") prop : String
                          , @Query("piprop") piProp : String, @Query("pithumbsize") piThumbSize : Int
                          , @Query("titles") titles: String): Deferred<JsonObject>
}