/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/4/19 8:18 PM
 *
 * Last modified 6/4/19 7:41 PM
 */

package com.appacea.twitterbell.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.VideoView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.appacea.twitterbell.R
import com.appacea.twitterbell.common.architecture.Resource
import com.appacea.twitterbell.common.architecture.Status
import com.appacea.twitterbell.common.extensions.attachSnapHelperWithListener
import com.appacea.twitterbell.data.tweet.network.TweetResponse
import com.appacea.twitterbell.data.tweet.network.TweetTestNetworkController
import com.appacea.twitterbell.utils.MapHelper
import com.appacea.twitterbell.utils.TwitterBellNetworkResponse

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import com.appacea.twitterbell.exceptions.TwitterBellNetworkError
import com.appacea.twitterbell.ui.main.adapters.TweetsAdapter
import com.appacea.twitterbell.common.extensions.dpToPx
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.appacea.twitterbell.data.tweet.network.TweetMedia
import com.appacea.twitterbell.ui.main.adapters.TweetsAdapterListener
import com.appacea.twitterbell.ui.main.custom.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt



class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener{

    private lateinit var tweetViewModel: TweetViewModel

    private lateinit var mMap: GoogleMap
    private var mMapHelper: MapHelper? = null

    private var tweets: List<Tweet> = ArrayList<Tweet>()
    private lateinit var draggingRecyclerview: DraggingRecyclerView

    private lateinit var searchbar: Searchbar

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        draggingRecyclerview = findViewById<DraggingRecyclerView>(R.id.drvBottom)
        val recyclerView = draggingRecyclerview.getRecyclerView()
        recyclerView.layoutManager = LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = TweetsAdapter(tweets,this@MapsActivity, object: TweetsAdapterListener{
            override fun onPhotoClicked(view: View?, media: TweetMedia?) {
                if (view != null && media != null) {
                    runOnUiThread{zoomImageFromThumb(view, media)}
                }
            }


        })
        recyclerView.attachSnapHelperWithListener(PagerSnapHelper(), SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, object:
            OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                val tweet = (recyclerView.adapter as TweetsAdapter).getTweet(position)
                print("tweet "+position)
                //move to tweet
                mMapHelper?.displayTweet(tweet)
            }

        })


        fabLocation = findViewById<FloatingActionButton>(R.id.fabLocation)
        fabLocation.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                targetLocation = currentLatLng
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }

        })



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tweetViewModel = ViewModelProviders.of(this).get(TweetViewModel::class.java)

        val tweetObserver = Observer<Resource<List<Tweet>>> { tweets ->


            if(tweets.status === Status.SUCCESS && tweets.data != null){
                //TODO: animate recyclerview open if closed
                //TODO: just update array and notify
                draggingRecyclerview.getRecyclerView().adapter = TweetsAdapter(tweets.data,this@MapsActivity, object: TweetsAdapterListener{
                    override fun onPhotoClicked(view: View?, media: TweetMedia?) {
                        if (view != null && media != null) {
                            runOnUiThread{zoomImageFromThumb(view, media)}
                        }
                    }
                })
            }

        }


        tweetViewModel.searchResult.observe(this, tweetObserver)

        searchbar = findViewById(R.id.searchbar)
        searchbar.setListener(object:SearchbarListener{
            override fun onSearch(query: String?) {
                val term = if(query==null) "" else query
                tweetViewModel.search(term)
            }
        })




    }

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
        mMapHelper = MapHelper(this,googleMap)


        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setPadding(0,0,0,100.dpToPx().roundToInt());
//        for(status in tweets.statuses){
//            mMapHelper?.addTweet(status)
//        }
       // mMapHelper.addTweet(Tweet())
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // 1
        mMap.isMyLocationEnabled = true

// 2
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

    }



    override fun onCameraMoveStarted(p0: Int) {
        fabLocation.show()
    }

    override fun onCameraIdle() {
        if(targetLocation!=null){
            targetLocation = null
            fabLocation.hide()
        }
        else{
            fabLocation.show()
        }
    }

    private var currentAnimator: Animator? = null
    private var shortAnimationDuration: Int = 100

    private fun zoomImageFromThumb(thumbView: View, media: TweetMedia) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        currentAnimator?.cancel()


        var view: View = findViewById(R.id.expanded_image)


        // Load the high-resolution "zoomed-in" image.
        val expandedImageView: ImageView = findViewById(R.id.expanded_image)
       // expandedImageView.setImageResource(imageResId)

        if(media.type == "video"){
            view = findViewById<VideoView>(R.id.expanded_video)
            val url = media.video_info.variants.get(0).url
            view.setVideoURI(Uri.parse(url))
            view.start()
        }else{
            Picasso.get().load(media.media_url).into(expandedImageView)
        }

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
                with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
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
                play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImageView.visibility = View.GONE
                        currentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImageView.visibility = View.GONE
                        currentAnimator = null
                    }
                })
                start()
            }
        }
    }


    //TODO: lifecycle stop location updates onstop onstart
}
