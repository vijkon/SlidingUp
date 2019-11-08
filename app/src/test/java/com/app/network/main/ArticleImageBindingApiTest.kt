package com.app.network.main

import com.app.mercari.util.SequenceList
import com.app.model.main.article.Image
import com.app.util.ConstantTest.Companion.articleImageErrorResponseFileName
import com.app.util.ConstantTest.Companion.articleImageSuccessResponseFileName
import com.app.util.ConstantTest.Companion.articleImageThumbnail
import com.app.util.JsonReader
import org.junit.Assert.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@RunWith(JUnit4::class)
class ArticleImageBindingApiTest
{
    @Mock
    private var articleImageBindingApi : ImgeOfArticleBindingApi? = null

    private lateinit var imageSuccess : Image

    private lateinit var imageError : Image

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val successResponse = JsonReader.readJSONFile(articleImageSuccessResponseFileName)

        val successJson = JSONObject(successResponse)

        imageSuccess = Image(successJson)

        val errorResponse = JsonReader.readJSONFile(articleImageErrorResponseFileName)

        val errorJson = JSONObject(errorResponse)

        imageError = Image(errorJson)
    }

    @Test
    fun `get article image by title and succeed`() {
        val response = Response.success(imageSuccess)
        runBlocking {
            `when`(articleImageBindingApi!!.getImageOfArticle(anyString(), anyString(), anyInt(),
                anyString(), anyString(), anyInt(), anyString())).thenAnswer(SequenceList(listOf(response)))
        }

        assertEquals(articleImageThumbnail, response.body()!!.imageThumbnail)
        assertFalse(response.body()!!.imageThumbnail[0].equals(""))
        assertNotNull(response.body()!!.imageThumbnail.length)
        assertNotEquals(response.body()!!.imageThumbnail.length, 0)
    }

    @Test(expected = Exception::class)
    fun `get article image by title and fail`() {
        val response = Response.success(imageError)
        runBlocking {
            `when`(articleImageBindingApi!!.getImageOfArticle(anyString(), anyString(), anyInt(),
                anyString(), anyString(), anyInt(), anyString())).thenAnswer(SequenceList(listOf(response)))
        }

        assertNull(response.body()!!.imageThumbnail.length)
        assertEquals(response.body()!!.imageThumbnail.length, 0)
        assertFalse(response.body()!!.imageThumbnail == articleImageThumbnail)
        assertNotEquals(response.body()!!.imageThumbnail.length, 1)
    }
}