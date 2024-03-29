/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/7/19 5:34 AM
 *
 * Last modified 6/7/19 5:34 AM
 */

package com.appacea.twitterbell.data.tweet.entities

data class SearchParams(val latitude:Double,val longitude:Double,val radius:Float,val query:String?)