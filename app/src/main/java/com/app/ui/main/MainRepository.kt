package com.app.ui.main

import androidx.lifecycle.MediatorLiveData
import com.app.extensionFunction.NonNullMediatorLiveData
import com.app.model.main.activity.NearByGeoArticle
import com.app.model.main.article.SelectedArticle
import com.app.network.main.ApiInterface
import com.app.util.Constants.Companion.formatTag
import com.app.util.Constants.Companion.geoSearchTag
import com.app.util.Constants.Companion.gsLimit
import com.app.util.Constants.Companion.gsRadius
import com.app.util.Constants.Companion.propTag
import com.app.util.Constants.Companion.queryTag
import com.app.util.NetworkState
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class MainRepository @Inject constructor(private val mainApi: ApiInterface)
{
    /**
     * Fetch a all Articles Data by NearByGeoArticle
     */

    suspend fun getAllArticles(articles: NonNullMediatorLiveData<NetworkState<NearByGeoArticle>>,
                               latitude: Double, longitude: Double): MediatorLiveData<NetworkState<NearByGeoArticle>>
    {
        withContext(Dispatchers.Main) {

            articles.value = NetworkState.Loading(null)
            val coordinates : String = doubleToString(latitude)+"|"+doubleToString(longitude)

            try
            {
                val response =  mainApi.getNearbyArticles(queryTag,geoSearchTag,gsRadius,
                    coordinates,gsLimit,formatTag).await()

                articles.value = NetworkState.Success(response)
            }
            catch (e: HttpException) {
                e.printStackTrace()
                articles.value = NetworkState.Error(e.code().toString(),null)
            }
        }
        return articles
    }

    /**
     * Fetch a Selected Marker Data using PageId by SelectedArticle
     */

    suspend fun getSpecificArticle(data: NonNullMediatorLiveData<NetworkState<SelectedArticle>>, pageId: Long)
            : MediatorLiveData<NetworkState<SelectedArticle>>
    {
        withContext(Dispatchers.Main) {

            data.value = NetworkState.Loading(null)

            try
            {
                val response =  mainApi.getArticleInfo(queryTag,propTag,pageId, formatTag).await()

                val json = JSONObject(response.toString())

                val articleData = SelectedArticle(json, pageId.toString())

                data.value = NetworkState.Success(articleData)
            }
            catch (e: HttpException) {
                e.printStackTrace()
                data.value = NetworkState.Error(e.code().toString(),null)
            }
        }

        return data
    }

    private fun doubleToString(value: Double) : String = value.toString()
}