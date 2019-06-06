/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 9:54 AM
 *
 * Last modified 6/6/19 9:54 AM
 */

package com.appacea.twitterbell.data.tweet.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.appacea.twitterbell.data.tweet.entities.Tweet

@Database(entities = [Tweet::class], version = 1, exportSchema = false)

@TypeConverters(value = [(Converters::class),(DoubleListConverter::class),(TweetEntityConverter::class),(GeoPolygonConverter::class)])
abstract class TweetDatabase : RoomDatabase() {

    abstract fun tweetDAO(): TweetDAO


    companion object {
        private var instance: TweetDatabase? = null
        fun getInstance(context: Context): TweetDatabase? {
            if (instance == null) {
                synchronized(TweetDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TweetDatabase::class.java, "tweet_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }
    }
}
