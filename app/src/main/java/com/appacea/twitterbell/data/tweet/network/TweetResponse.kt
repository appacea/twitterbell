/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/4/19 11:32 PM
 *
 * Last modified 6/4/19 11:32 PM
 */

package com.appacea.twitterbell.data.tweet.network

import com.appacea.twitterbell.data.tweet.entities.GeoPoint
import com.appacea.twitterbell.data.tweet.entities.GeoPolygon
import com.appacea.twitterbell.data.tweet.entities.Tweet

data class TweetResponse(val statuses:List<Tweet>)

data class TweetStatus(val created_at:String, val id:Long, val text:String, val geo: GeoPoint, val coordinates: GeoPoint, val place:TweetPlace, val user:TweetUser, val entities:TweetExtended?)

data class TweetPlace(val id:String, val bounding_box: GeoPolygon)

data class TweetUser(val id:String, val profile_image_url: String, val name:String, val screen_name:String)

data class TweetExtended(val media:List<TweetMedia>)

data class TweetMedia(val media_url:String, val type:String, val video_info:TweetMediaVideo)

data class TweetMediaVideo(val variants:List<TweetMediaVideoVariant>)

data class TweetMediaVideoVariant(val url:String)