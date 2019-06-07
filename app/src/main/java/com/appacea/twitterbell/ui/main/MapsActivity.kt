/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/4/19 8:18 PM
 *
 * Last modified 6/4/19 7:41 PM
 */

package com.appacea.twitterbell.ui.main

import android.animation.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.appacea.twitterbell.R
import com.appacea.twitterbell.common.architecture.Resource
import com.appacea.twitterbell.common.architecture.Status
import com.appacea.twitterbell.common.extensions.attachSnapHelperWithListener
import com.appacea.twitterbell.utils.MapHelper

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import com.appacea.twitterbell.ui.main.adapters.TweetsAdapter
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.appacea.twitterbell.data.tweet.network.NetworkResponse
import com.appacea.twitterbell.data.tweet.network.TweetMedia
import com.appacea.twitterbell.data.user.User
import com.appacea.twitterbell.exceptions.DialogHelper
import com.appacea.twitterbell.exceptions.ErrorHandling
import com.appacea.twitterbell.ui.login.LoginActivity
import com.appacea.twitterbell.ui.main.adapters.TweetsAdapterListener
import com.appacea.twitterbell.ui.main.custom.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.twitter.sdk.android.core.TwitterCore
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.dialog_radius.view.*



class MapsActivity : AppCompatActivity(), OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolygonClickListener{

    private lateinit var user: User
    private lateinit var tweetViewModel: TweetViewModel

    private var currentPosition: Int = 0 //The current position in bottom listview
    private lateinit var mMap: GoogleMap //Google MAp
    private var mMapHelper: MapHelper? = null //Helper for Google Map

    private var tweets: ArrayList<Tweet> = ArrayList<Tweet>() //Store list of tweets visible
    private lateinit var draggingRecyclerview: DraggingRecyclerView
    private lateinit var draggingRecyclerviewAdapter: TweetsAdapter

    private lateinit var searchbar: Searchbar

    //Location aware fields
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var fabLocation: FloatingActionButton
    private var targetLocation: LatLng? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        user = User.getCurrentUser(this)!!
        nav_view.setNavigationItemSelectedListener(this)

        //Setup recyclerview and adapter listener
        draggingRecyclerview = findViewById<DraggingRecyclerView>(R.id.drvBottom)
        val recyclerView = draggingRecyclerview.getRecyclerView()
        recyclerView.layoutManager = LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false)
        draggingRecyclerviewAdapter = TweetsAdapter(tweets,this@MapsActivity, object: TweetsAdapterListener{
            override fun onFavoriteClicked(tweet: Tweet?) {
                tweet?.let { tweetViewModel.favorite(it) }
            }

            override fun onRetweetClicked(tweet: Tweet?) {
                tweet?.let { tweetViewModel.retweet(it) }
            }

            override fun onPhotoClicked(view: View?, media: TweetMedia?) {
                if (view != null && media != null) {
                    runOnUiThread{zoomImageFromThumb(view, media)}
                }
            }
        })
        recyclerView.adapter = draggingRecyclerviewAdapter

                //Setup listener for open / close of bottom recyclerview
        draggingRecyclerview.setListener(object:DraggingRecyclerViewListener{
            override fun onClose(height:Int) {
                //when closed show full search area
                user.getLastSearch()?.let { mMapHelper?.zoomToSearchParams(it) }
                //Setup logo padding (abide by google terms)
                mMap.setPadding(0,0,0,height)
            }

            override fun onOpen() {
                //move camera to current item
                mMapHelper?.displayTweet(draggingRecyclerviewAdapter.getTweet(currentPosition), null)
                //Setup logo padding (abide by google terms)
                mMap.setPadding(0,0,0,draggingRecyclerview.height)
            }

        })

        //Add Pager style snapping to recyclerview
        recyclerView.attachSnapHelperWithListener(PagerSnapHelper(), SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, object:
            OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                if(position>0){
                    currentPosition = position
                    val tweet = (recyclerView.adapter as TweetsAdapter).getTweet(position)
                    val geoExists = mMapHelper?.displayTweet(tweet,user.getLastSearch())
                    if(geoExists != true){
                        Toast.makeText(this@MapsActivity, getString(R.string.maps_activity_nogeo), Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })

        //Setup floating action button for location
        fabLocation = findViewById(R.id.fabLocation)
        fabLocation.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                if (ActivityCompat.checkSelfPermission(this@MapsActivity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MapsActivity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                    return
                }
                if (::lastLocation.isInitialized) {
                    val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                    targetLocation = currentLatLng
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                }else{
                    ErrorHandling.showErrorDialog(this@MapsActivity,getString(R.string.maps_activity_nolocation))
                }
            }

        })


        //Setup location tracking
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Setup viewmodel
        tweetViewModel = ViewModelProviders.of(this).get(TweetViewModel::class.java)
        val tweetObserver = Observer<Resource<List<Tweet>>> { tweets ->
            if(tweets.status === Status.SUCCESS && tweets.data != null){
                if(mMapHelper!=null){
                    this.tweets.clear()
                    this.tweets.addAll(tweets.data)
                    draggingRecyclerview.getRecyclerView().adapter?.notifyDataSetChanged()
                    for(i in this.tweets.indices){
                        mMapHelper?.addTweets(this.tweets)
                    }

                    if(this.tweets.size==0){
                        draggingRecyclerview.hide()
                        mMap.setPadding(0,0,0,0)
                    }else{
                        draggingRecyclerview.show()
                        mMap.setPadding(0,0,0,draggingRecyclerview.BOTTOM_HEIGHT)

                    }
                }
            }
            else if(tweets.status == Status.ERROR){
                ErrorHandling.showErrorDialog(this,null)
            }

        }

        //Observe changes in search
        tweetViewModel.searchResult.observe(this, tweetObserver)
        //Observe results from retweet
        tweetViewModel.retweetResult.observe(this, Observer<NetworkResponse<Boolean>>{it->
            if(it.isFailure){
                ErrorHandling.showErrorDialog(this@MapsActivity,it.message)
            }
            else{
                DialogHelper.showDialog(this@MapsActivity, getString(R.string.maps_activity_retweeted_dialog_title),getString(R.string.maps_activity_retweeted_dialog_message))
            }
        })
        //Observer results from favorite
        tweetViewModel.favoriteResult.observe(this, Observer<NetworkResponse<Boolean>>{it->
            if(it.isFailure){
                ErrorHandling.showErrorDialog(this@MapsActivity,it.message)
            }
            else{
                DialogHelper.showDialog(this@MapsActivity, getString(R.string.maps_activity_favorite_dialog_title),getString(R.string.maps_activity_favorite_dialog_message))
            }
        })

        //Setup searchbar
        searchbar = findViewById(R.id.searchbar)
        searchbar.setListener(object:SearchbarListener{
            override fun onMenuClicked() {
                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                }else{
                    drawer_layout.openDrawer(GravityCompat.START)
                }
            }

            override fun onSearch(query: String?) {
                if (ActivityCompat.checkSelfPermission(this@MapsActivity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MapsActivity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                    searchbar.clear() //TODO: execute search after permissions instead of this
                    return
                }
                if (::lastLocation.isInitialized) {
                    val lastSearch = SearchParams(lastLocation.latitude,lastLocation.longitude,user.getRadius(),query)
                    user.setLastSearch(lastSearch)
                    tweetViewModel.search(lastSearch)
                }else{
                    ErrorHandling.showErrorDialog(this@MapsActivity,getString(R.string.maps_activity_nolocation))
                }
            }
        })


        tweetViewModel.
    }






    /**
     * Display the radius modification dialog
     *
     * @param radius - the radis to display as a string
     */
    fun showRadiusDialog(radius:String){
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_radius, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle(getString(R.string.radius_dialog_title))
        val  mAlertDialog = mBuilder.show()
        mDialogView.dialogSaveBtn.setOnClickListener {
            user.setRadius(mDialogView.etRadius.text.toString().toFloat())
            mAlertDialog.dismiss()
        }
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.etRadius.setText(radius)
    }

    /********************************************************************************
     *
     * Google Maps Listeners
     *
     *******************************************************************************/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //hide location button we made our own
        mMap.uiSettings.isMyLocationButtonEnabled = false

        //init logo to bottom of draggingrecyclerview
        //TODO: set padding based on position of recyclerview
        //mMap?.setPadding(0,0,0,draggingRecyclerview.BOTTOM_HEIGHT)

        //Configure map helper
        mMapHelper = MapHelper(this,googleMap)

        //Add listeners
        mMap.setOnPolygonClickListener (this)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setOnCameraMoveStartedListener(this)


        //Get permissions for location tracking
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        else{
            //Setup location tracking
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                }
            }
        }

    }



    /**
     * onMarkerClick
     *
     * When Marker is clicked scroll recyclerview to tweet item
     */
    override fun onMarkerClick(marker: Marker?): Boolean {
        val index = marker?.tag as Int
        draggingRecyclerview.getRecyclerView().scrollToPosition(index)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 20f))
        draggingRecyclerview.open()//open if not already
        return true
    }

    /**
     * onPolygonClick
     *
     * When Polygon is clicked scroll recyclerview to tweet item
     */
    override fun onPolygonClick(polygon: Polygon?) {
        val index = polygon?.tag as Int
        draggingRecyclerview.getRecyclerView().scrollToPosition(index)
    }

    /**
     * If moving the camera show the location button so we can click to get back to current location
     */
    override fun onCameraMoveStarted(p0: Int) {
        fabLocation.show()
    }

    /**
     * If the camera stops moving and we are at our location then hide the location button
     */
    override fun onCameraIdle() {
        if(targetLocation!=null){
            targetLocation = null
            fabLocation.hide()
        }
        else{
            fabLocation.show()
        }
    }




    /********************************************************************************
     *
     * Activity Navigation
     *
     *******************************************************************************/

    /**
     * If back is pressed and drawer is open then close it
     */
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    /**
     * Manage drawer clicks
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.navLogout -> {
                TwitterCore.getInstance().sessionManager.clearActiveSession()
                val intent = Intent(this@MapsActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.navRadius -> {
                showRadiusDialog(user.getRadius().toString())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /********************************************************************************
     *
     * Activity Lifecycle
     *
     *******************************************************************************/


    //TODO: lifecycle stop location updates onstop onstart



    /********************************************************************************
     *
     * Image/Video Zoom
     *
     *******************************************************************************/


    private var currentAnimator: Animator? = null
    private var shortAnimationDuration: Int = 100

    /**
     * Zoom from a thumbnail to the larger image or video
     *
     * @param thumbnail - the view to zoom into
     * @param media - the media to display (photo or video from tweet)
     */
    private fun zoomImageFromThumb(thumbView: View, media: TweetMedia) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        currentAnimator?.cancel()


        var view: MediaView = findViewById(R.id.expanded_media)
        view.setMedia(media)


        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBoundsInt)
        findViewById<View>(R.id.container)
            .getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        val startScale: Float
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.alpha = 0f
        view.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        view.pivotX = 0f
        view.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        currentAnimator = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(
                view,
                View.X,
                startBounds.left,
                finalBounds.left)
            ).apply {
                with(ObjectAnimator.ofFloat(view, View.Y, startBounds.top, finalBounds.top))
                with(ObjectAnimator.ofFloat(view, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(view, View.SCALE_Y, startScale, 1f))
            }
            duration = shortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    currentAnimator = null
                }
            })
            start()
        }

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        view.setOnClickListener {
            currentAnimator?.cancel()

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            currentAnimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(view, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(view, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(view, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(view, View.SCALE_Y, startScale))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        thumbView.alpha = 1f
                        view.visibility = View.GONE
                        currentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        thumbView.alpha = 1f
                        view.visibility = View.GONE
                        currentAnimator = null
                    }
                })
                start()
            }
        }
    }


    /********************************************************************************
     *
     * Permissions
     *
     *******************************************************************************/

    //TODO: DIALOG EXPLAINING WHY WE NEED LOCATION DATA (BEST PRACTICE)
    /**
     * If we did not get permissions then display a message
     */
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    //Setup location tracking
                    mMap.isMyLocationEnabled = true
                    fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                        if (location != null) {
                            lastLocation = location
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                        }
                    }

                } else {
                    Toast.makeText(this, getString(R.string.maps_activity_nopermissions), Toast.LENGTH_SHORT).show();
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


}
