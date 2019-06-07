/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/4/19 8:19 PM
 *
 * Last modified 6/4/19 8:19 PM
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
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.google.gson.Gson
import com.twitter.sdk.android.core.TwitterCore
import java.util.HashMap

/**
 * Volley implementation of network controller
 *
 */
class TweetVolleyNetworkController constructor(context: Context): TweetNetworkController{


    private var cache: DiskBasedCache
    private var network:BasicNetwork = BasicNetwork(HurlStack())
    private var requestQueue: RequestQueue


    /**
     * Init cache and request queue for volley
     */
    init{
        this.cache = DiskBasedCache(context.getCacheDir(), 1024 * 1024) // 1MB cap
        this.requestQueue = RequestQueue(cache, network).apply {
            start()
        }
    }


    /**
     * Return the Authorization header for the given url and method
     * @param url - the url endpoint
     * @param method - the type of call (GET, POST..etc)
     */
    fun getAuthorizationHeader(url:String, method:String): String{
        val oAuth1aParameters = OAuth1aParameters(
            TwitterCore.getInstance().authConfig,
            TwitterCore.getInstance().sessionManager.activeSession.authToken,null,method,url,null)
        return oAuth1aParameters.authorizationHeader
    }

    /**
     * Get tweets from twitter API based on query within a circle
     *
     * @param params - the SearchParams for the query
     */
    override fun getTweets(params: SearchParams): LiveData<NetworkResponse<TweetResponse>> {
        val data = MutableLiveData<NetworkResponse<TweetResponse>>()
        val url = "https://api.twitter.com/1.1/search/tweets.json?q=${params.query}&geocode=${params.latitude},${params.longitude},${params.radius}km&result_type=recent"
        val stringRequest = object: StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                data.value = NetworkResponse(Gson().fromJson(response, TweetResponse::class.java))
            },
            Response.ErrorListener { error:VolleyError?->
                data.value = NetworkResponse(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = getAuthorizationHeader(url,"GET")
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }
        this.requestQueue.add(stringRequest)
        return data
    }


    /**
     * Retweet a tweet
     *
     * @param tweet - the tweet to retweet
     */
    override fun retweet(tweet: Tweet): LiveData<NetworkResponse<Boolean>> {
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
                headers["Authorization"] = getAuthorizationHeader(url,"POST")
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }
        this.requestQueue.add(stringRequest)
        return data
    }

    /**
     * Favorite a tweet
     *
     * @param tweet - the tweet to favorite
     */
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
}