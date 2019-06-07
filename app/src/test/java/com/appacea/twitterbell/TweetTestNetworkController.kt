/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/7/19 4:19 PM
 *
 * Last modified 6/7/19 3:21 PM
 */

package com.appacea.twitterbell

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.*
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.appacea.twitterbell.data.tweet.network.NetworkResponse
import com.appacea.twitterbell.data.tweet.network.TweetNetworkController
import com.appacea.twitterbell.data.tweet.network.TweetResponse
import com.google.gson.Gson
import com.twitter.sdk.android.core.TwitterCore
import java.io.InputStream
import java.security.SecureRandom
import java.util.*


/**
 * Test controller that uses local asset to generate tweet data
 *
 *
 */
class TweetTestNetworkController constructor(context: Context):
    TweetNetworkController {
    override fun favoriteTweet(tweet: Tweet): LiveData<NetworkResponse<Boolean>> {
        val data = MutableLiveData<NetworkResponse<Boolean>>()
        val id = tweet.id
        val url = "https://api.twitter.com/1.1/favorites/create.json?id=${id}"
        val stringRequest = object: StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                data.value = NetworkResponse(true)
            },
            Response.ErrorListener { error:VolleyError?->
                data.value = NetworkResponse(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = getAuthorizationHeader(url,"POST")
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }
        this.requestQueue.add(stringRequest)
        return data
    }

    private val RAND = SecureRandom()

    private val context: Context = context

    private var cache: DiskBasedCache
    private var network:BasicNetwork = BasicNetwork(HurlStack())
    private var requestQueue: RequestQueue

    init{
        this.cache = DiskBasedCache(context.getCacheDir(), 1024 * 1024) // 1MB cap
        this.requestQueue = RequestQueue(cache, network).apply {
            start()
        }
    }

    fun getAuthorizationHeader(url:String, method:String): String{
        val oAuth1aParameters = OAuth1aParameters(
            TwitterCore.getInstance().authConfig,
            TwitterCore.getInstance().sessionManager.activeSession.authToken, null, method, url, null
        )
        return oAuth1aParameters.authorizationHeader
    }

    fun readJSONFromAsset(): String? {
        var json: String? = null
        try {
            val  inputStream: InputStream = context.assets.open("twitter2.json")
            json = inputStream.bufferedReader().use{it.readText()}
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    override fun getTweets(params: SearchParams):LiveData<NetworkResponse<TweetResponse>> {
        val data = MutableLiveData<NetworkResponse<TweetResponse>>()
        data.value = NetworkResponse(
            Gson().fromJson(
                readJSONFromAsset(),
                TweetResponse::class.java
            )
        )
        return data
    }

    override fun retweet(tweet: Tweet):LiveData<NetworkResponse<Boolean>> {
        val data = MutableLiveData<NetworkResponse<Boolean>>()
        val id = tweet.id
        val url = "https://api.twitter.com/1.1/statuses/retweet/$id.json"
        val stringRequest = object: StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                data.value = NetworkResponse(true)
            },
            Response.ErrorListener { error:VolleyError?->
                data.value = NetworkResponse(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = getAuthorizationHeader(url, "POST")
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }
        this.requestQueue.add(stringRequest)
        return data
    }

}