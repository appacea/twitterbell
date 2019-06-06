/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 5:16 PM
 *
 * Last modified 6/6/19 5:05 PM
 */

package com.appacea.twitterbell.data.tweet.entities


data class GeoPoint(val type:String= "Point",val coordinates:List<Double>)