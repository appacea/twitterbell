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
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.google.gson.Gson
import com.twitter.sdk.android.core.TwitterCore
import java.io.InputStream
import java.security.SecureRandom
import java.util.*

class TweetTestNetworkController constructor(context: Context): TweetNetworkController{

    private val RAND = SecureRandom()

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

    fun getAuthorizationHeader(url:String): String{
        val oAuth1aParameters = OAuth1aParameters(TwitterCore.getInstance().authConfig,TwitterCore.getInstance().sessionManager.activeSession.authToken,null,"POST",url,null)
        return oAuth1aParameters.authorizationHeader
//        val token = TwitterCore.getInstance().sessionManager.activeSession.authToken.token
//        //717657645503610880-QM1Y87Gt9NUnzbmleoxXmf3j4NjGgC1
//        val timestamp = Date().time
//        return "OAuth oauth_consumer_key=\"sTf6wKkiArDZP72end7DA7rOr\",oauth_token=\"717657645503610880-QM1Y87Gt9NUnzbmleoxXmf3j4NjGgC1\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"${getTimestamp}\",oauth_nonce=\"${getNonce()}\",oauth_version=\"1.0\",oauth_signature=\"e6jkeuC80MWmqSRpgaPB7r4wJzU%3D\""
    }

    private fun getNonce(): String {
        return System.nanoTime().toString() + (Math.abs(RAND.nextLong()))
    }

    private fun getTimestamp(): String {
        val secondsFromEpoch = System.currentTimeMillis() / 1000
        return java.lang.Long.toString(secondsFromEpoch)
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
        data.value = NetworkResponse(Gson().fromJson(readJSONFromAsset(), TweetResponse::class.java))
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
                headers["Authorization"] = getAuthorizationHeader(url)
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }
        this.requestQueue.add(stringRequest)
        return data
    }

}