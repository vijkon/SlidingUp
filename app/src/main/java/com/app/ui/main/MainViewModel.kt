package com.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.extensionFunction.NonNullMediatorLiveData
import com.app.model.main.activity.NearByGeoArticle
import com.app.model.main.article.SelectedArticle
import com.app.util.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val mainRepository : MainRepository) : ViewModel()
{
    // FOR DATA ---
    private val articles = NonNullMediatorLiveData<NetworkState<NearByGeoArticle>>()
    val nearbyArticles : LiveData<NetworkState<NearByGeoArticle>> = articles

    /**
     * Fetch a all Articles Data by NearByGeoArticle
     */

    fun getNearbyArticles(latitude : Double, longitude : Double)
    {
        CoroutineScope(Dispatchers.IO).launch {
            mainRepository.getAllArticles(articles,latitude,longitude)
        }
    }

    // FOR DATA ---
    private val specificData = NonNullMediatorLiveData<NetworkState<SelectedArticle>>()
    val specificArticles : LiveData<NetworkState<SelectedArticle>> = specificData

    /**
     * Fetch a Selected Marker Data using PageId by SelectedArticle
     */

    fun getFetchSpecificArticle(pageId : Long)
    {
        CoroutineScope(Dispatchers.IO).launch {
            mainRepository.getSpecificArticle(specificData,pageId)
        }
    }
}