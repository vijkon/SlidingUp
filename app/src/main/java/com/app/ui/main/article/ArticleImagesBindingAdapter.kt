package com.app.ui.main.article

import android.net.Uri
import android.text.TextUtils
import androidx.databinding.BindingAdapter
import com.app.model.main.article.FetchArticleImages
import com.app.model.main.article.Image
import com.app.network.main.ImgeOfArticleBindingApi
import com.app.util.Constants.Companion.formatTag
import com.app.util.Constants.Companion.imagesPropTag
import com.app.util.Constants.Companion.piPropTag
import com.app.util.Constants.Companion.queryTag
import com.app.util.Constants.Companion.thumbNailTag
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

class ArticleImagesBindingAdapter (private val fresco: PipelineDraweeControllerBuilder, private val articleImageBindingApi: ImgeOfArticleBindingApi)
{
    @BindingAdapter("articleItemImage")
    fun articleItemImage(imageView: SimpleDraweeView, articleImages : FetchArticleImages)
    {
        GlobalScope.launch {
            try {
                val response = articleImageBindingApi.getImageOfArticle(queryTag,formatTag,2
                    ,imagesPropTag,piPropTag,thumbNailTag,articleImages.title).await()

                withContext(Dispatchers.Main) {

                    val json = JSONObject(response.toString())

                    val thumbnail = Image(json)

                    if(!TextUtils.isEmpty(thumbnail.imageThumbnail))
                    {
                        val draweeController = fresco.setImageRequest(ImageRequest.fromUri(Uri.parse(thumbnail.imageThumbnail)))
                            .setOldController(imageView.controller).build()
                        imageView.controller = draweeController
                    }
                }
            }
            catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }
}