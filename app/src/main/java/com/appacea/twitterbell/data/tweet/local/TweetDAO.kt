/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 9:53 AM
 *
 * Last modified 6/6/19 9:53 AM
 */

package com.appacea.twitterbell.data.tweet.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appacea.twitterbell.data.tweet.entities.Tweet

@Dao
interface TweetDAO{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTweets(tweets: List<Tweet>): LongArray

    @Query("SELECT * FROM `Tweet`")
    fun getTweets(): LiveData<List<Tweet>>


    @Query("DELETE FROM `Tweet`")
    fun deleteTweets()
}