package com.app.network.main

import com.app.mercari.util.SequenceList
import com.app.model.main.activity.ArticleActivity
import com.app.model.main.activity.FetchGeoLocationList
import com.app.model.main.activity.NearByGeoArticle
import com.app.model.main.article.SelectedArticle
import com.app.util.ConstantTest.Companion.pageId
import com.app.util.ConstantTest.Companion.specificArticleErrorResponseFileName
import com.app.util.ConstantTest.Companion.specificArticleSuccessResponseFileName
import com.app.util.JsonReader
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.*
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
class MainApiTest
{
    @Mock
    private var mainApi: ApiInterface? = null

    private lateinit var nearByGeoArticleSuccess : NearByGeoArticle

    private lateinit var nearByGeoArticleError : NearByGeoArticle

    private lateinit var articleDataSuccess : SelectedArticle

    private lateinit var articleDataError : SelectedArticle

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        nearByGeoArticleSuccess = NearByGeoArticle(
            ArticleActivity
            (
                mutableListOf(
                    FetchGeoLocationList(18806750,"Sports Hall",60.183333333333,24.925555555556),
                    FetchGeoLocationList(537137,"Finnish National Opera and Ballet",60.181666666667,24.929722222222),
                    FetchGeoLocationList(4852943,"Pallokentt",60.185833333333,24.924722222222),
                    FetchGeoLocationList(1190033,"Helsinki Olympic Stadium",60.186944444444,24.927222222222),
                    FetchGeoLocationList(549280,"",60.179166666667,24.923888888889),
                    FetchGeoLocationList(43213041,"Church",60.179722222222,24.921111111111),
                    FetchGeoLocationList(2914754,"Telia 5G -areena",60.1875,24.9225)
                )
            )
        )

        nearByGeoArticleError = NearByGeoArticle(ArticleActivity(mutableListOf()))

        val successResponse = JsonReader.readJSONFile(specificArticleSuccessResponseFileName)

        val successJson = JSONObject(successResponse)

        articleDataSuccess = SelectedArticle(successJson, pageId)

        val errorResponse = JsonReader.readJSONFile(specificArticleErrorResponseFileName)

        val errorJson = JSONObject(errorResponse)

        articleDataError = SelectedArticle(errorJson, "0")
    }

    @Test
    fun `search articles through user location and succeed`() {
        val response = Response.success(nearByGeoArticleSuccess)

        runBlocking {
            `when`(mainApi!!.getNearbyArticles(anyString(), anyString(), anyLong(),
                anyString(), anyInt(), anyString())).thenAnswer(SequenceList(listOf(response)))
        }

        assertEquals(7, response.body()!!.query.geoSearchList.size)
        assertEquals("Church", response.body()!!.query.geoSearchList[5].title)
        assertEquals(549280, response.body()!!.query.geoSearchList[4].pageId)
        assertTrue(response.body()!!.query.geoSearchList[2].latitude == 60.185833333333)
        assertTrue(response.body()!!.query.geoSearchList[2].longitude == 24.924722222222)
        assertNotNull("",response.body()!!.query.geoSearchList[4].title)
        assertFalse(response.body()!!.query.geoSearchList[4].title == "Sports Hall")
        assertFalse(response.body()!!.query.geoSearchList[4].latitude == 60.1875)
    }

    @Test(expected = Exception::class)
    fun `search articles through user location and fail`() {
        val response = Response.success(nearByGeoArticleError)

        runBlocking {
            `when`(mainApi!!.getNearbyArticles(anyString(), anyString(), anyLong(),
                anyString(), anyInt(), anyString())).thenAnswer(SequenceList(listOf(response)))
        }

        assertEquals(0, response.body()!!.query.geoSearchList.size)
        assertEquals(500000,response.body()!!.query.geoSearchList[4].pageId)
        assertTrue(response.body()!!.query.geoSearchList.isNullOrEmpty())
        assertFalse(response.body()!!.query.geoSearchList.isNotEmpty())
    }

    @Test
    fun `search specific articles by PageId and succeed`() {
        val response = Response.success(articleDataSuccess)

        runBlocking {
            `when`(mainApi!!.getArticleInfo(anyString(), anyString(),
                anyLong(), anyString())).thenAnswer(SequenceList(listOf(response)))
        }

        assertEquals("File:Hiekkaharju station.jpg", response.body()!!.fetchArticleImagesList[2].title)
        assertEquals("Hiekkaharju", response.body()!!.title)
        assertTrue(response.body()!!.description == "City District")
        assertFalse(response.body()!!.descriptionSource == "Internet")
        assertNotNull(response.body()!!.fetchArticleImagesList.size)
        assertNotEquals(response.body()!!.contentModel,"")
    }

    @Test
    fun `search specific articles by PageId and fail`() {
        val response = Response.success(articleDataError)

        runBlocking {
            `when`(mainApi!!.getArticleInfo(anyString(), anyString(),
                anyLong(), anyString())).thenAnswer(SequenceList(listOf(response)))
        }

        assertEquals(0, response.body()!!.fetchArticleImagesList.size)
        assertEquals(null, response.body()!!.title)
        assertTrue(response.body()!!.description == null)
        assertFalse(response.body()!!.descriptionSource == "Internet")
        assertNotEquals(response.body()!!.contentModel,"")
    }
}