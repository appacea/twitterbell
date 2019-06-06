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
import com.appacea.twitterbell.data.tweet.network.TweetStatus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.appacea.twitterbell.common.extensions.getBounds
import com.appacea.twitterbell.common.extensions.getZoomLevel
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.google.android.gms.maps.model.*


class MapHelper constructor(context: Context,map:GoogleMap){

    private val map: GoogleMap = map
    private val context: Context = context


    fun addTweet(tweet: TweetStatus){


        //Show marker if there is location data




        val img = BitmapFactory.decodeResource(context.getResources(), com.appacea.twitterbell.R.drawable.twitter_social_icon_circle_color)
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(img)

        val islamabad = LatLng(tweet.geo.coordinates.get(1), tweet.geo.coordinates.get(0))
        val markerOptions = MarkerOptions()
        markerOptions.position(islamabad)
            .title("Location Details")
            .snippet("I am custom Location Marker.")
           // .icon(bitmapDescriptor)

            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))


        map.addMarker(markerOptions)
        map.moveCamera(CameraUpdateFactory.newLatLng(islamabad))
    }


    fun displayTweet(tweet: Tweet){
        //Move to marker, if place then display box, if none display "no geo data"
        if(tweet.geo!=null){
            val position = LatLng(tweet.geo.coordinates.get(1), tweet.geo.coordinates.get(0))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position,5f))

        }
        else if(tweet.place!=null){
            val coordinates = tweet.place.bounding_box.coordinates
            val rectOptions = PolygonOptions()
            for(coordinate in coordinates.get(0)){
                rectOptions.add(LatLng(coordinate.get(1), coordinate.get(0)))
            }
            val polygon = map.addPolygon(rectOptions)
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(polygon.getBounds(), 200));

        }
        else{
            val circleOptions = CircleOptions()
            circleOptions.center(LatLng(45.50884, -73.58781))
                .radius(5.0)
                .strokeWidth(10f)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(128, 255, 0, 0))
            val circle = map.addCircle(circleOptions)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(circle.center, circle.getZoomLevel()));
        }

    }

}