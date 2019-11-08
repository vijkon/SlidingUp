package com.app.ui.main.article

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.app.extensionFunction.nonNull
import com.app.slidingup.R
import com.app.ui.main.maps.AnchorSheetBehavior
import com.app.util.Constants
import com.app.util.NetworkState
import com.app.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.model.main.article.FetchArticleImages
import com.app.model.main.maps.PolylineData
import com.app.slidingup.databinding.ArticleFragmentBinding
import com.app.ui.main.MainViewModel
import com.app.ui.main.maps.GoogleMapHelper
import com.app.ui.main.maps.PolyLineViewModel
import com.app.util.Constants.Companion.latTag
import com.app.util.Constants.Companion.lngTag
import com.app.util.UiHelper
import com.google.android.gms.maps.model.Polyline
import java.util.ArrayList
import kotlin.math.roundToInt

class ArticleFragment(private val mMap: GoogleMap, private val markerLatLng: LatLng, private val currentLatLng: LatLng)
    : DaggerFragment() , View.OnClickListener
{
    // FOR DATA ---
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var googleMapHelper: GoogleMapHelper
    @Inject lateinit var uiHelper : UiHelper
    private lateinit var mainViewModel : MainViewModel
    private lateinit var polyLineViewModel: PolyLineViewModel
    private val articleImagesAdapter = ArticleImagesAdapter()
    private var mBottomSheet: LinearLayout? = null
    private var binding : ArticleFragmentBinding? = null
    private var bottomSheetBehavior : AnchorSheetBehavior<View>? = null
    private var blackPolyLine : Polyline? = null
    private var greyPolyLine : Polyline? = null
    private val listLatLng = ArrayList<LatLng>()
    private var pageId : Long = 0
    private var recyclerView : RecyclerView? = null
    private var routeInfoLL : LinearLayout? = null
    private var articleLL : LinearLayout? = null
    private var articlePB : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageId = arguments!!.getLong(Constants.pageId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = ArticleFragmentBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this

        val view = binding!!.root

        // find container view
        mBottomSheet = view.findViewById(R.id.bottom_sheet)

        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        val getThereBtn = view.findViewById(R.id.getThereBtn) as Button
        getThereBtn.setOnClickListener(this)

        val routeSuggestionBtn = view.findViewById(R.id.routeSuggestionBtn) as Button
        routeSuggestionBtn.setOnClickListener(this)

        routeInfoLL = view.findViewById(R.id.routeInfo_layout) as LinearLayout
        articleLL = view.findViewById(R.id.article_layout) as LinearLayout
        articlePB = view.findViewById(R.id.article_pb) as ProgressBar

        initializeBottomSheet()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        /*
        * Initialize the ViewModel
        * */

        mainViewModel = ViewModelProviders.of(this,providerFactory).get(MainViewModel::class.java)

        polyLineViewModel = ViewModelProviders.of(this,providerFactory).get(PolyLineViewModel::class.java)

        initRecyclerView()
        if(pageId != 0.toLong()) {
            if(uiHelper.getConnectivityStatus())
                subscribeObservers()
            else
                uiHelper.toast(resources.getString(R.string.error_network_connection))
        }
    }

    //Setup the adapter class for the RecyclerView
    private fun initRecyclerView()
    {
        recyclerView!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.adapter = articleImagesAdapter
    }

    private fun subscribeObservers()
    {
        // OBSERVABLES ---
        mainViewModel.getFetchSpecificArticle(pageId)
        mainViewModel.specificArticles.nonNull().observe(this, Observer {
            when(it)
            {
                is NetworkState.Loading ->  uiHelper.showProgressBar(articlePB!!,true)
                is NetworkState.Success -> {
                    if(it.data != null) {
                        uiHelper.showProgressBar(articlePB!!,false)
                        binding!!.articles = it.data

                        if(it.data.fetchArticleImagesList.size >= 1) {
                            recyclerView!!.visibility = View.VISIBLE
                            articleImagesAdapter.updateData(it.data.fetchArticleImagesList as ArrayList<FetchArticleImages>)
                        }
                        else
                            recyclerView!!.visibility = View.GONE
                    }
                }
                is NetworkState.Error -> uiHelper.showProgressBar(articlePB!!,false)
            }
        })
    }

    // Init the bottom sheet behavior
    private fun initializeBottomSheet()
    {
        bottomSheetBehavior = AnchorSheetBehavior.from(mBottomSheet)

        if(pageId != 0.toLong()) {
            bottomSheetBehavior!!.state = AnchorSheetBehavior.STATE_COLLAPSED

            bottomSheetBehavior!!.setAnchorOffset(0.5f)
            bottomSheetBehavior!!.setAnchorSheetCallback(object : AnchorSheetBehavior.AnchorSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val h = bottomSheet.height.toFloat()
                    val off = h * slideOffset

                    when (bottomSheetBehavior!!.state) {
                        AnchorSheetBehavior.STATE_DRAGGING -> moveMarkerToCenter(off)
                        AnchorSheetBehavior.STATE_SETTLING -> moveMarkerToCenter(off)
                    }
                }
            })
        }
        else {
            clearPloyLineAnimation()
            bottomSheetBehavior!!.peekHeight = 0
            bottomSheetBehavior!!.state = AnchorSheetBehavior.STATE_COLLAPSED
        }
    }

    // Reposition marker at the center
    private fun moveMarkerToCenter(off: Float) {
        setMapPaddingBottom(off)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng))
    }

    private fun setMapPaddingBottom(offset: Float?) {
        val maxMapPaddingBottom = 1.0f
        mMap.setPadding(0, 0, 0, (offset!! * maxMapPaddingBottom).roundToInt())
    }

    // Removing the PloyLine Animation
    private fun clearPloyLineAnimation()
    {
        if(blackPolyLine != null && greyPolyLine != null)
        {
            setVisibility(articleLL!!,routeInfoLL!!)
            blackPolyLine!!.remove()
            greyPolyLine!!.remove()
            this.listLatLng.clear()
        }
    }

    override fun onClick(view : View?) {
        when(view!!.id)
        {
            R.id.getThereBtn -> {
                bottomSheetBehavior!!.state = AnchorSheetBehavior.STATE_COLLAPSED

                if(currentLatLng != markerLatLng) {
                    clearPloyLineAnimation()

                    if(uiHelper.getConnectivityStatus())
                        subscribePolyLineObserver(markerLatLng,currentLatLng)
                    else
                        uiHelper.toast(resources.getString(R.string.error_network_connection))
                }
            }
            R.id.routeSuggestionBtn -> {
                if(listLatLng.size >= 1)
                {
                    bottomSheetBehavior!!.state = AnchorSheetBehavior.STATE_COLLAPSED
                    setVisibility(routeInfoLL!!,articleLL!!)
                }
            }
        }
    }

    // UPDATE UI ----
    private fun setVisibility(firstView : View,secondView : View) {
        firstView.visibility = View.VISIBLE
        secondView.visibility = View.GONE
    }

    // Subscribe the PolyLine Observer to get the Route info using LatLng
    private fun subscribePolyLineObserver(markerLatLng: LatLng, currentLatLng: LatLng)
    {
        uiHelper.showProgressBar(articlePB!!,true)
        polyLineViewModel.getPolyline(markerLatLng,currentLatLng)
        // OBSERVABLES ---
        polyLineViewModel.polyLineList.nonNull().observe(this, Observer {
            if(it != null)
            {
                uiHelper.showProgressBar(articlePB!!,false)
                drawPolyline(it)
            }
        })
    }

    private fun drawPolyline(result : List<PolylineData>) {
        val points: ArrayList<LatLng> = ArrayList()

        // Traversing through all the routes
        for (i in result.indices)
        {
            val routeInfoData = result[i].routeInfo

            binding!!.routeInfo = routeInfoData

            // Fetching i-th route
            val path = result[i].ployLineRoutesList

            // Fetching all the points in i-th route
            for (j in path.indices) {
                val point = path[j]

                val lat = java.lang.Double.parseDouble(point[latTag]!!)
                val lng = java.lang.Double.parseDouble(point[lngTag]!!)
                val position = LatLng(lat, lng)

                points.add(position)
            }

            this.listLatLng.addAll(points)
        }

        blackPolyLine = mMap.addPolyline(googleMapHelper.getPolyLineOptions(Color.BLACK))
        greyPolyLine = mMap.addPolyline(googleMapHelper.getPolyLineOptions(Color.GRAY))

        if(blackPolyLine != null && greyPolyLine != null && listLatLng.size >= 1)
            googleMapHelper.animatePolyLine(blackPolyLine!!,greyPolyLine!!,listLatLng)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearPloyLineAnimation()
    }
}
