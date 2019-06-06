/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 5:05 PM
 *
 * Last modified 6/6/19 5:05 PM
 */

package com.appacea.twitterbell.data.tweet.local

import androidx.room.TypeConverter
import com.appacea.twitterbell.data.tweet.entities.GeoPolygon
import com.appacea.twitterbell.data.tweet.entities.TweetEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object Converters {
    @TypeConverter
    fun fromTimestamp(value: String): ArrayList<String> {
        val listType = object : TypeToken<ArrayList<String>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun arraylistToString(list: ArrayList<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}


open class GeoPolygonConverter {
    @TypeConverter
    fun fromString(value: String): GeoPolygon? {
        val listType = object : TypeToken<GeoPolygon>() {}.type
        return Gson().fromJson<GeoPolygon>(value, listType)
    }

    @TypeConverter
    fun fromList(list: GeoPolygon): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}

open class DoubleListConverter {
    @TypeConverter
    fun fromString(value: String): List<Double>? {
        val listType = object : TypeToken<List<Double>>() {}.type
        return Gson().fromJson<List<Double>>(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Double>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
open class TweetEntityConverter {
    @TypeConverter
    fun fromString(value: String): TweetEntity? {
        val listType = object : TypeToken<TweetEntity>() {}.type
        return Gson().fromJson<TweetEntity>(value, listType)
    }

    @TypeConverter
    fun fromList(list: TweetEntity): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}