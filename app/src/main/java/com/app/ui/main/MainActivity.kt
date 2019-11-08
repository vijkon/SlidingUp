package com.app.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.app.extensionFunction.nonNull
import com.app.listeners.ListenerInterface
import com.app.location.GpsSetting
import com.app.location.LocationViewModel
import com.app.slidingup.R
import com.app.ui.main.article.ArticleFragment
import com.app.ui.main.maps.GoogleMapHelper
import com.app.util.Constants.Companion.GPS_REQUEST_LOCATION
import com.app.util.Constants.Companion.pageId
import com.app.util.NetworkState
import com.app.util.PermissionHelper
import com.app.util.PermissionHelper.Companion.ACCESS_FINE_LOCATION
import com.app.util.PermissionHelper.Companion.PERMISSIONS_REQUEST_LOCATION
import com.app.util.UiHelper
import com.app.viewmodels.ViewModelProviderFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), OnMapReadyCallback,PermissionHelper.OnPermissionRequested
    ,GoogleMap.OnMarkerClickListener
{
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var uiHelper : UiHelper
    @Inject lateinit var googleMapHelper: GoogleMapHelper
    private lateinit var mainViewModel : MainViewModel
    private lateinit var locationViewModel : LocationViewModel
    private var mMap: GoogleMap? = null
    private var permissionHelper : PermissionHelper? = null
    private var gpsSetting : GpsSetting? = null
    private var currentLatLng : LatLng? = null
    private var isPermissionPermanentlyDenied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPlayServicesAvailable()

        gpsSetting = GpsSetting(this,uiHelper)

        permissionHelper = PermissionHelper(this,uiHelper)

        /*
        * Initialize the ViewModel
        * */

        mainViewModel = ViewModelProviders.of(this,providerFactory).get(MainViewModel::class.java)
        locationViewModel = ViewModelProviders.of(this,providerFactory).get(LocationViewModel::class.java)

        if(!permissionHelper!!.isPermissionGranted(ACCESS_FINE_LOCATION)) {
            permissionHelper!!.requestPermission(arrayOf(ACCESS_FINE_LOCATION),PERMISSIONS_REQUEST_LOCATION,this)
        }
        else
            enableGps()
    }

    /*
     * Checking out is Google Play Services app is installed or not.
     * */

    private fun checkPlayServicesAvailable()
    {
        if(!uiHelper.isPlayServicesAvailable()) {
            uiHelper.toast(resources.getString(R.string.play_service_not_installed))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if(isPermissionPermanentlyDenied)
            checkPermissionGranted()
    }

    /*
     * Checking whether Location Permission is granted or not.
     * */

    private fun checkPermissionGranted()
    {
        if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionHelper!!.openSettingsDialog()
        else
            enableGps()
    }

    override fun onMapReady(googleMap : GoogleMap?) {
        mMap = googleMap
        googleMapHelper.defaultMapSettings(mMap!!)
        mMap!!.setOnMarkerClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * This function is to get the result form [PermissionHelper] class
     *
     * @param isPermissionGranted the [Boolean]
     */

    override fun onPermissionResponse(isPermissionGranted: Boolean) {

        if(!isPermissionGranted)
            isPermissionPermanentlyDenied = true
        else
            enableGps()
    }

    private fun enableGps()
    {
        isPermissionPermanentlyDenied = false

        if (!uiHelper.isLocationProviderEnabled())
            subscribeLocationObserver()
        else
            gpsSetting!!.openGpsSettingDialog()
    }

    // Start Observing the User Current Location and set the marker to it.
    private fun subscribeLocationObserver()
    {
        uiHelper.showProgressBar(progress_bar,true)

        // OBSERVABLES ---
        locationViewModel.currentLocation.nonNull().observe(this, Observer {

            uiHelper.showProgressBar(progress_bar,false)
            currentLatLng = googleMapHelper.getLatLng(it.latitude,it.longitude)

            mapSetUp(currentLatLng!!)

            locationViewModel.stopLocationUpdates()
        })

        locationViewModel.requestLocationUpdates()
    }

    // Add a marker to the current Location, and move the camera.
    private fun mapSetUp(latLing : LatLng) {
        if(mMap != null)
        {
            mMap!!.addMarker(googleMapHelper.addCurrentLocationMarker(latLing))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLing, 14.0F))

            if(uiHelper.getConnectivityStatus()) {
                mainViewModel.getNearbyArticles(latLing.latitude,latLing.longitude)
                // OBSERVABLES ---
                mainViewModel.nearbyArticles.nonNull().observe(this,Observer{

                    when(it)
                    {
                        is NetworkState.Loading ->  uiHelper.showProgressBar(progress_bar,true)

                        is NetworkState.Success -> {
                            if(it.data != null) {

                                uiHelper.showProgressBar(progress_bar,false)

                                for (i in 0 until it.data.query.geoSearchList.size)
                                    mMap!!.addMarker(googleMapHelper.addMarker(it.data.query.geoSearchList[i])).tag = it.data.query.geoSearchList[i].pageId
                            }
                        }
                        is NetworkState.Error -> uiHelper.showProgressBar(progress_bar,false)
                    }
                })
            }
            else
                uiHelper.showSnackBar(mainActivityRootView,resources.getString(R.string.error_network_connection))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)
        {
            GPS_REQUEST_LOCATION ->
                when (resultCode)
                {
                    RESULT_OK -> subscribeLocationObserver()

                    RESULT_CANCELED -> {
                        uiHelper.showPositiveDialogWithListener(this,
                            resources.getString(R.string.need_location),
                            resources.getString(R.string.location_content),
                            object : ListenerInterface {
                                override fun onPositive() {
                                    enableGps()
                                }
                            }, resources.getString(R.string.turn_on), false)
                    }
                }
        }
    }

    // onMarkerClick will return the details of selected Marker like its position,title and description if any
    override fun onMarkerClick(marker: Marker): Boolean
    {
        if(marker.tag == null || marker.tag == 0.toLong())
            marker.tag = 0.toLong()
        else
            googleMapHelper.changeMarkerColor(marker)

        val fragment = ArticleFragment(mMap!!, marker.position,currentLatLng!!)
        val args = Bundle()
        args.putLong(pageId, marker.tag as Long)
        fragment.arguments = args

        supportFragmentManager.beginTransaction().replace(R.id.main_fragment_container,fragment).commit()

        return false
    }
}
