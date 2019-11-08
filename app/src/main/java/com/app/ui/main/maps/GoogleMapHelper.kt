package com.app.ui.main.maps

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.app.model.main.activity.FetchGeoLocationList
import com.app.slidingup.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.util.ArrayList

class GoogleMapHelper {

    private var lastClicked : Marker?  = null

    /**
     * @param geoSearchList where to draw the [Marker]
     * @return the [MarkerOptions] with given properties added to it.
     */

    fun addMarker(geoSearchList: FetchGeoLocationList): MarkerOptions = MarkerOptions().position(LatLng(geoSearchList.latitude, geoSearchList.longitude))
        .anchor(0.5f, 0.5f).title(geoSearchList.title).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_black))

    /**
     * @param latLng where to draw the current location [Marker]
     * @return the [MarkerOptions] with given properties added to it.
     */

    fun addCurrentLocationMarker(latLng : LatLng): MarkerOptions = MarkerOptions().position(latLng)

    /**
     * @param latitude
     * @param longitude
     * @return the [LatLng]
     */

    fun getLatLng(latitude : Double, longitude : Double) = LatLng(latitude, longitude)

    /**
     * This function sets the default google map settings.
     *
     * @param googleMap to set default settings.
     */

    fun defaultMapSettings(googleMap: GoogleMap) {
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = true
        googleMap.uiSettings.isTiltGesturesEnabled = true
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.isBuildingsEnabled = true
    }

    /**
     * This function sets the marker color based on the click of marker
     *
     * @param marker the [Marker]
     */

    fun changeMarkerColor(marker: Marker)
    {
        if (lastClicked != null)
            lastClicked!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_black))

        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_blue))

        lastClicked = marker
    }

    /**
     * This function is to define the Polyline UI Property
     *
     * @param color the [Int]
     * @return the [PolylineOptions]
     */

    fun getPolyLineOptions(color: Int): PolylineOptions
    {
        val options = PolylineOptions()
        options.width(10f)
        options.color(color)
        options.startCap(SquareCap())
        options.endCap(SquareCap())
        options.jointType(JointType.ROUND)

        return options
    }

    /**
     * This function is to animate the PolyLine drawn b/w 2 points
     *
     * @param blackPolyLine the [Polyline]
     * @param greyPolyLine the [Polyline]
     * @param listLatLng the [ArrayList[LatLng]]
     */

    fun animatePolyLine(blackPolyLine: Polyline, greyPolyLine: Polyline, listLatLng: ArrayList<LatLng>)
    {
        val valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator.duration = 1000
        valueAnimator.interpolator = LinearInterpolator()

        valueAnimator.addUpdateListener { animator ->
            val latLngList = blackPolyLine.points
            val initialPointSize = latLngList.size
            val animatedValue = animator.animatedValue as Int
            val newPoints = animatedValue * listLatLng.size / 100

            if (initialPointSize < newPoints) {
                latLngList.addAll(listLatLng.subList(initialPointSize, newPoints))
                blackPolyLine.points = latLngList
            }
        }

        valueAnimator.addListener(object : Animator.AnimatorListener
        {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator)
            {
                val blackLatLng = blackPolyLine.points
                val greyLatLng = greyPolyLine.points

                greyLatLng.clear()
                greyLatLng.addAll(blackLatLng)
                blackLatLng.clear()

                blackPolyLine.points = blackLatLng
                greyPolyLine.points = greyLatLng

                blackPolyLine.zIndex = 2f

                animator.start()
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })

        valueAnimator.start()
    }
}