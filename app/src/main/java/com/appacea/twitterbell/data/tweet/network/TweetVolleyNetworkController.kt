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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.*
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.appacea.twitterbell.exceptions.TwitterBellNetworkError
import com.appacea.twitterbell.utils.TwitterBellNetworkResponse
import com.google.gson.Gson

class TweetVolleyNetworkController constructor(context: Context): TweetNetworkController{


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

/*
    override fun getTweets(listener: TwitterBellNetworkResponse<TweetResponse>){
        val url = "https://api.twitter.com/1.1/search/tweets.json?q=&geocode=-22.912214,-43.230182,1km&lang=pt&result_type=recent"
        val stringRequest = object: StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                listener.onResponse(Gson().fromJson(response, TweetResponse::class.java))
            },
            Response.ErrorListener { error:VolleyError?->
                listener.onError(TwitterBellNetworkError())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "OAuth oauth_consumer_key=\"sTf6wKkiArDZP72end7DA7rOr\",oauth_token=\"717657645503610880-QM1Y87Gt9NUnzbmleoxXmf3j4NjGgC1\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"1559704879\",oauth_nonce=\"elN708nolqG\",oauth_version=\"1.0\",oauth_signature=\"e6jkeuC80MWmqSRpgaPB7r4wJzU%3D\""
                return headers
            }
        }
        this.requestQueue.add(stringRequest)
    }
*/
    override fun getTweets(params: SearchParams): LiveData<NetworkResponse<TweetResponse>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retweet(tweet: Tweet): LiveData<NetworkResponse<Boolean>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}