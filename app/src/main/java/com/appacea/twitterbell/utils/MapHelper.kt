/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/4/19 9:31 PM
 *
 * Last modified 6/4/19 9:31 PM
 */

package com.appacea.twitterbell.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.appacea.twitterbell.common.extensions.getBounds
import com.appacea.twitterbell.common.extensions.getZoomLevel
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.google.android.gms.maps.model.*

/**
 * MapHelper
 *
 *
 * A helper class to manage custom map functions
 */
class MapHelper constructor(context: Context, map:GoogleMap){

    private val shapes: HashMap<Tweet,Any> = HashMap<Tweet,Any>()
    private val map: GoogleMap = map //a reference to the map
    private val context: Context = context //the context linked to the map
    private var searchCircle: Circle? = null
    /**
     * Adds a list of tweets to the map
     *
     * @param tweets
     */
    fun addTweets(tweets: List<Tweet>){
        map.clear()
        for(i in tweets.indices){
            addTweet(i,tweets[i])
        }
    }

    /**
     * Add a tweet to the map with a index reference
     *
     * @param index - the index of the tweet (reference in a list)
     * @param tweet - the tweet
     */
    fun addTweet(index:Int,tweet:Tweet){
        if(tweet.geo!=null){
            val img = BitmapFactory.decodeResource(context.getResources(), com.appacea.twitterbell.R.drawable.twitter_bird)
            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(img)
            val location = LatLng(tweet.geo.coordinates.get(0), tweet.geo.coordinates.get(1))
            val markerOptions = MarkerOptions()
            markerOptions.position(location)
                .icon(bitmapDescriptor)
              //  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            val marker = map.addMarker(markerOptions)
            marker.tag = index
            shapes.put(tweet,marker)
        }
        else if(tweet.place!=null){
            val coordinates = tweet.place.bounding_box.coordinates
            val rectOptions = PolygonOptions().clickable(true).strokeColor(Color.BLUE)
                .strokeWidth(5f)
            for(coordinate in coordinates.get(0)){
                rectOptions.add(LatLng(coordinate.get(1), coordinate.get(0)))
            }
            val polygon = map.addPolygon(rectOptions)
            polygon.tag = index
            shapes.put(tweet,polygon)
        }
    }


    /**
     * Move the camera to the tweet geo position
     *
     * Not all tweets have a GeoPoint so we will display in this order:
     * - If GeoPoint exists move to lat/lon
     * - If Place exists move to bbox
     * - If no geo info exists move to the original search circle
     *
     * @param tweet - the tweet to move to
     * @param params - the search params to default the move to
     *
     * @return - true if geo data exists
     */
    fun displayTweet(tweet: Tweet?, params:SearchParams?):Boolean{
        if(tweet == null) return false
        //If search circle is showing remove it
        searchCircle?.let {it.remove()}

        var shape = shapes.get(tweet)
        if(shape is Marker){
            val position = LatLng(shape.position.latitude,shape.position.longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position,map.maxZoomLevel))
            return true
        }
        else if(shape is Polygon){
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(shape.getBounds(), 200))
            return true
        }
        else if(params != null){
            zoomToSearchParams(params)
        }
        return false
    }

    /**
     * Zoom the map to a given search parameter
     *
     * @param params - the search params to zoom to
     */
    fun zoomToSearchParams(params: SearchParams){
        //Show the search circle since the tweet came from somewhere in the area
        val circleOptions = CircleOptions()
        circleOptions.center(LatLng(params.latitude, params.longitude))
            .radius(params.radius.toDouble()*1000)
            .strokeWidth(10f)
            .strokeColor(Color.BLUE)
        searchCircle = map.addCircle(circleOptions)
        map.animateCamera(searchCircle?.getZoomLevel()?.let {
            CameraUpdateFactory.newLatLngZoom(searchCircle?.center,
                it
            )
        });
    }
}
