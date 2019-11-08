package com.app.di.module.main

import com.app.network.main.ApiInterface
import com.app.network.maps.MapApi
import com.app.ui.main.article.ArticleImagesAdapter
import com.app.ui.main.MainRepository
import com.app.ui.main.maps.GoogleMapHelper
import com.app.ui.main.maps.PolyLineRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule
{
    @Module
    companion object
    {
        @MainScope
        @Provides
        @JvmStatic
        fun provideMainApi(retrofit: Retrofit) : ApiInterface = retrofit.create(ApiInterface::class.java)

        @MainScope
        @Provides
        @JvmStatic
        fun provideArticleImagesAdapter() = ArticleImagesAdapter()

        @MainScope
        @Provides
        @JvmStatic
        fun provideMainRepository(mainApi: ApiInterface) = MainRepository(mainApi)

        @MainScope
        @Provides
        @JvmStatic
        fun provideMapApi(retrofit: Retrofit) : MapApi = retrofit.create(MapApi::class.java)

        @MainScope
        @Provides
        @JvmStatic
        fun provideGoogleMapRepository(mapApi: MapApi) = PolyLineRepository(mapApi)

        @MainScope
        @Provides
        @JvmStatic
        fun googleMapHelper() = GoogleMapHelper()
    }
}