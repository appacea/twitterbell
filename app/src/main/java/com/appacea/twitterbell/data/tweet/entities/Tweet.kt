/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 5:16 PM
 *
 * Last modified 6/6/19 5:15 PM
 */

package com.appacea.twitterbell.data.tweet.entities

import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

//TODO: store query?
@Entity(primaryKeys = ["id"])
data class Tweet(
    @SerializedName("id") val id: Long,
    val text: String,
    @Embedded(prefix = "geo_") val geo: GeoPoint?,
    @Embedded(prefix = "coordinate_") val coordinates: GeoPoint?,

    @Embedded(prefix = "place_") val place: TweetPlace?,
    @Embedded(prefix = "user_") val user: TweetUser?,
    val entities: TweetEntity?

)