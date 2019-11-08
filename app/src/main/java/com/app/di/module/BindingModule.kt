package com.app.di.module

import com.app.di.annotations.DataBinding
import com.app.network.main.ImgeOfArticleBindingApi
import com.app.ui.main.article.ArticleImagesBindingAdapter
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object BindingModule
{
    @Provides
    @DataBinding
    fun provideArticleImageBindingApi(retrofit: Retrofit) : ImgeOfArticleBindingApi = retrofit.create(ImgeOfArticleBindingApi::class.java)

    @Provides
    @DataBinding
    fun provideMainBindingAdapter(fresco: PipelineDraweeControllerBuilder,articleImageBindingApi: ImgeOfArticleBindingApi) : ArticleImagesBindingAdapter =
        ArticleImagesBindingAdapter(fresco, articleImageBindingApi)
}