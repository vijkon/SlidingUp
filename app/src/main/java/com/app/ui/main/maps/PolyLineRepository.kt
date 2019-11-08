package com.app.ui.main.maps

import androidx.lifecycle.MediatorLiveData
import com.app.extensionFunction.NonNullMediatorLiveData
import com.app.model.main.maps.PolylineData
import com.app.model.main.maps.RouteData
import com.app.network.maps.MapApi
import com.app.slidingup.BuildConfig.DIRECTION_API_URL
import com.app.slidingup.BuildConfig.MAP_API_kEY
import com.app.util.Constants.Companion.destinationTag
import com.app.util.Constants.Companion.distanceTag
import com.app.util.Constants.Companion.durationTag
import com.app.util.Constants.Companion.endAddressTag
import com.app.util.Constants.Companion.formatTag
import com.app.util.Constants.Companion.ketTag
import com.app.util.Constants.Companion.latTag
import com.app.util.Constants.Companion.legsTag
import com.app.util.Constants.Companion.lngTag
import com.app.util.Constants.Companion.originTag
import com.app.util.Constants.Companion.pointsTag
import com.app.util.Constants.Companion.polylineTag
import com.app.util.Constants.Companion.routesTag
import com.app.util.Constants.Companion.sensorTag
import com.app.util.Constants.Companion.startAddressTag
import com.app.util.Constants.Companion.stepsTag
import com.app.util.Constants.Companion.textTag
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject

class PolyLineRepository @Inject constructor(private val mapApi: MapApi)
{
    suspend fun getPolyLineData(data : NonNullMediatorLiveData<List<PolylineData>>,
                                markerLatLng : LatLng, currentLatLng : LatLng)
            : MediatorLiveData<List<PolylineData>>
    {
        // Origin of route
        val strOrigin = originTag + markerLatLng.latitude + "," + markerLatLng.longitude

        // Destination of route
        val strDest = destinationTag + currentLatLng.latitude + "," + currentLatLng.longitude

        val key = ketTag+""+MAP_API_kEY

        // Building the parameters to the web service
        val parameters = "$strOrigin&$strDest&$sensorTag&$key"

        // Building the url to the web service
        val directionApiUrl = "$DIRECTION_API_URL$formatTag?$parameters"

        withContext(Dispatchers.Main) {

            try
            {
                val response = mapApi.getPolylineDataAsync(directionApiUrl).await()

                val gsonData : JsonObject = JsonParser().parse(response.toString()).asJsonObject

                data.value = parseJsonData(JSONObject(gsonData.toString()))
            }
            catch (e: HttpException)
            {
                e.printStackTrace()
            }
        }

        return data
    }

    private fun parseJsonData(jObject: JSONObject): List<PolylineData> {

        val routes = ArrayList<PolylineData>()
        val jRoutes: JSONArray
        var jLegs: JSONArray
        var jSteps: JSONArray

        try
        {
            jRoutes = jObject.getJSONArray(routesTag)

            /** Traversing all routes  */
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes.get(i) as JSONObject).getJSONArray(legsTag)

                val path = ArrayList<HashMap<String, String>>()

                /** Traversing all legs  */
                for (j in 0 until jLegs.length())
                {
                    val distance = (jLegs.get(j) as JSONObject).getJSONObject(distanceTag).getString(textTag)
                    val duration = (jLegs.get(j) as JSONObject).getJSONObject(durationTag).getString(textTag)
                    val endAddress = (jLegs.get(j) as JSONObject).getString(endAddressTag)
                    val startAddress = (jLegs.get(j) as JSONObject).getString(startAddressTag)

                    val routeInfoData = RouteData(distance,duration,endAddress,startAddress)

                    jSteps = (jLegs.get(j) as JSONObject).getJSONArray(stepsTag)

                    /** Traversing all steps  */
                    for (k in 0 until jSteps.length()) {
                        val polyline = ((jSteps.get(k) as JSONObject).get(polylineTag) as JSONObject).get(pointsTag) as String
                        val list = decodePoly(polyline)

                        /** Traversing all points  */
                        for (l in list.indices) {
                            val hm = HashMap<String, String>()
                            hm[latTag] = list[l].latitude.toString()
                            hm[lngTag] = list[l].longitude.toString()
                            path.add(hm)
                        }
                    }

                    val polylineData = PolylineData(routeInfoData,path)

                    routes.add(polylineData)
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        return routes
    }

    private fun decodePoly(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }
}