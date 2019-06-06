/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 5:16 PM
 *
 * Last modified 6/6/19 5:05 PM
 */

package com.appacea.twitterbell.data.tweet.entities

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class Place(

    @SerializedName("id")
    val id: Long,

    @Embedded(prefix = "bbox_")  val bounding_box: GeoPoint
)