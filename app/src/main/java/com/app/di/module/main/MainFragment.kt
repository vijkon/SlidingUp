package com.app.di.module.main

import com.app.ui.main.article.ArticleFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class MainFragment
{
    @ContributesAndroidInjector
    abstract fun getMainFragment() : ArticleFragment
}