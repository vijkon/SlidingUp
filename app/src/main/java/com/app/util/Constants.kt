package com.app.util

class Constants
{
    companion object
    {
        const val TIMEOUT_REQUEST : Long = 30

        const val GPS_REQUEST_LOCATION = 1000

        const val pageId : String = "pageId"

        const val queryTag = "query"

        const val geoSearchTag = "geosearch"

        const val gsRadius : Long = 10000

        const val gsLimit = 50

        const val formatTag = "json"

        const val propTag = "info|description|images"

        const val imagesPropTag = "pageimages|pageterms"

        const val piPropTag = "thumbnail"

        const val thumbNailTag = 150

        /* SelectedArticle Response Tags */

        const val pagesTag = "pages"

        const val titleTag = "title"

        const val contentModelTag = "contentmodel"

        const val descriptionTag = "description"

        const val descriptionSourcetag = "descriptionsource"

        const val imagesTag = "images"

        const val sourceTag = "source"

        /* Google direction api Tags */

        const val originTag = "origin="

        const val destinationTag = "destination="

        const val sensorTag = "sensor=false"

        const val ketTag = "key="

        /* Google direction api Response Tags */

        const val routesTag = "routes"

        const val legsTag = "legs"

        const val distanceTag = "distance"

        const val textTag = "text"

        const val durationTag = "duration"

        const val endAddressTag = "end_address"

        const val startAddressTag = "start_address"

        const val stepsTag = "steps"

        const val polylineTag = "polyline"

        const val pointsTag = "points"

        const val latTag = "lat"

        const val lngTag = "lng"
    }
}