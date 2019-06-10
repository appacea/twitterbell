/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 9:57 AM
 *
 * Last modified 6/6/19 9:57 AM
 */

package com.appacea.twitterbell.data.tweet.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.appacea.twitterbell.common.architecture.NetworkBoundResource
import com.appacea.twitterbell.common.architecture.Resource
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.appacea.twitterbell.data.tweet.local.TweetDAO
import com.appacea.twitterbell.data.tweet.network.NetworkResponse
import com.appacea.twitterbell.data.tweet.network.TweetNetworkController
import com.appacea.twitterbell.data.tweet.network.TweetResponse


class TweetRepository(
    private val tweetDAO: TweetDAO,
    private val tweetNetworkController: TweetNetworkController
) {


    fun retweet(tweet:Tweet):LiveData<NetworkResponse<Boolean>>{
        return tweetNetworkController.retweet(tweet)
    }

    fun favorite(tweet:Tweet):LiveData<NetworkResponse<Boolean>>{
        return tweetNetworkController.favoriteTweet(tweet)
    }

    fun loadTweets(params: SearchParams) : LiveData<Resource<List<Tweet>>> {
        return object : NetworkBoundResource<List<Tweet>, TweetResponse>() {

            override fun saveCallResult(item: TweetResponse) {
                tweetDAO.deleteTweets()
                tweetDAO.insertTweets(item.statuses)
            }

            override fun shouldFetch(data: List<Tweet>?): Boolean {
                return true
            }

            override fun createCall(): LiveData<NetworkResponse<TweetResponse>> {
                return tweetNetworkController.getTweets(params)
            }

            override fun loadFromDb(): LiveData<List<Tweet>> {
                return tweetDAO.getTweets()
            }



        }.asLiveData()
    }

    fun clearTweets(){
        AsyncTask.execute {
            tweetDAO.deleteTweets()
        }
    }
}


