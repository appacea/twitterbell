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
import com.appacea.twitterbell.common.architecture.Resource
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.appacea.twitterbell.data.tweet.local.TweetDatabase
import com.appacea.twitterbell.data.tweet.network.TweetTestNetworkController
import com.appacea.twitterbell.data.tweet.repository.TweetRepository

class TweetViewModel(application: Application): AndroidViewModel(application) {
//@Inject constructor(
//    tweetDao: TweetDAO,
//    tweetNetworkController: TweetNetworkController)
    private val tweetRepository: TweetRepository //= TweetRepository(TweetDatabase., tweetNetworkController)

    /* We are using LiveData to update the UI with the data changes.
     */
   // private val tweetsListLiveData = MutableLiveData<Resource<List<Tweet>>>()


    init{
        val database: TweetDatabase = TweetDatabase.getInstance(
            application.applicationContext
        )!!
        tweetRepository = TweetRepository(database.tweetDAO(), TweetTestNetworkController(application.applicationContext))
    }

    val searchInput: MutableLiveData<String> = MutableLiveData()


    val searchResult = Transformations.switchMap(searchInput){
        if(it.length >= 1) {
            tweetRepository.loadTweets(it)
        } else {
            MutableLiveData<Resource<List<Tweet>>>()
        }
    }

    fun search(term: String){
        searchInput.value = (term)
    }

    fun retweet(tweet:Tweet) {
        retweetInput.value = (tweet)
    }

    val retweetInput: MutableLiveData<Tweet> = MutableLiveData()

    val retweetResult = Transformations.switchMap(retweetInput){
        it -> tweetRepository.retweet(it)
    }

    /*
     * LiveData observed by the UI
     * */
  //  fun getTweetsLiveData() = tweetRepository.loadTweets("")
}
