/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/4/19 8:07 PM
 *
 * Last modified 6/4/19 8:07 PM
 */

package com.appacea.twitterbell.data.tweet.network

import androidx.lifecycle.LiveData
import com.appacea.twitterbell.common.architecture.Resource
import com.appacea.twitterbell.data.tweet.Tweet
import com.appacea.twitterbell.utils.TwitterBellNetworkResponse

interface TweetNetworkController{

    fun getTweets(listener: TwitterBellNetworkResponse<TweetResponse>)
    fun getTweets(): LiveData<NetworkResponse<TweetResponse>>
}