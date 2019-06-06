/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 9:48 AM
 *
 * Last modified 6/6/19 9:48 AM
 */

package com.appacea.twitterbell.data.tweet.local

import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["id"])
data class Tweet(
    @SerializedName("id") val id: Long,
    @Embedded(prefix = "place_") val place: Place,
    @Embedded(prefix = "user_") val user: User
)