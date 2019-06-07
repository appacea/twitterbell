/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 1:46 PM
 *
 * Last modified 6/6/19 1:46 PM
 */

package com.appacea.twitterbell.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.appacea.twitterbell.data.tweet.local.TweetDatabase
import com.appacea.twitterbell.data.tweet.network.TweetTestNetworkController
import com.appacea.twitterbell.data.tweet.network.TweetVolleyNetworkController
import com.appacea.twitterbell.data.tweet.repository.TweetRepository

class TweetViewModel(application: Application): AndroidViewModel(application) {
    private val tweetRepository: TweetRepository

    init{
        val database: TweetDatabase = TweetDatabase.getInstance(
            application.applicationContext
        )!!
        tweetRepository = TweetRepository(database.tweetDAO(), TweetVolleyNetworkController(application.applicationContext))
    }

    //Search livedata
    val searchInput: MutableLiveData<SearchParams> = MutableLiveData()
    val searchResult = Transformations.switchMap(searchInput){
        it->tweetRepository.loadTweets(it)
    }
    fun search(params: SearchParams){
        searchInput.value = (params)
    }

    //Retweet livedata
    fun retweet(tweet:Tweet) {
        retweetInput.value = (tweet)
    }
    val retweetInput: MutableLiveData<Tweet> = MutableLiveData()
    val retweetResult = Transformations.switchMap(retweetInput){
        it -> tweetRepository.retweet(it)
    }


    //Favorite livedata
    fun favorite(tweet:Tweet) {
        favoriteTweet.value = (tweet)
    }
    val favoriteTweet: MutableLiveData<Tweet> = MutableLiveData()
    val favoriteResult = Transformations.switchMap(favoriteTweet){
            it -> tweetRepository.favorite(it)
    }

}
