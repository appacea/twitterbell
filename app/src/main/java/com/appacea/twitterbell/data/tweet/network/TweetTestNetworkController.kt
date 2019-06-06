/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 10:49 AM
 *
 * Last modified 6/4/19 11:56 PM
 */

package com.appacea.twitterbell.data.tweet.network

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.*
import com.appacea.twitterbell.common.architecture.Resource
import com.appacea.twitterbell.data.tweet.Tweet
import com.appacea.twitterbell.exceptions.TwitterBellNetworkError
import com.appacea.twitterbell.utils.TwitterBellNetworkResponse
import com.google.gson.Gson
import java.io.InputStream

class TweetTestNetworkController constructor(context: Context): TweetNetworkController{

    private val context: Context = context
    private val baseUrl = "https://api.twitter.com/1.1/tweets/search/"
    private val apiKey = "sTf6wKkiArDZP72end7DA7rOr"
    private val apiSecret = "ITzScolzRyMU0QgSEHjxmYMN9U3UobIahyYlqApgU7jkvYjXoz"

    private var cache: DiskBasedCache
    private var network:BasicNetwork = BasicNetwork(HurlStack())
    private var requestQueue: RequestQueue

    init{
        this.cache = DiskBasedCache(context.getCacheDir(), 1024 * 1024) // 1MB cap
        this.requestQueue = RequestQueue(cache, network).apply {
            start()
        }
    }

    fun readJSONFromAsset(): String? {
        var json: String? = null
        try {
            val  inputStream: InputStream = context.assets.open("twitter.json")
            json = inputStream.bufferedReader().use{it.readText()}
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    override fun getTweets(listener: TwitterBellNetworkResponse<TweetResponse>){

        listener.onResponse(Gson().fromJson(readJSONFromAsset(), TweetResponse::class.java))
    }

    override fun getTweets():LiveData<NetworkResponse<TweetResponse>> {
        val data = MutableLiveData<NetworkResponse<TweetResponse>>()
        data.value = NetworkResponse(Gson().fromJson(readJSONFromAsset(), TweetResponse::class.java))
        return data
    }
}