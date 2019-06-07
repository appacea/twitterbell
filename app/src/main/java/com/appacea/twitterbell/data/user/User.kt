/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 10:21 PM
 *
 * Last modified 6/6/19 10:21 PM
 */

package com.appacea.twitterbell.data.user

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.appacea.twitterbell.data.tweet.entities.SearchParams
import com.appacea.twitterbell.data.tweet.local.TweetDatabase
import com.twitter.sdk.android.core.TwitterCore

val PREFS_FILENAME = "com.appacea.twitterbell.prefs"
val PREFS_RADIUS = "com.appacea.twitterbell.prefs.radius"

class User{

    val prefs: SharedPreferences
    private var lastSearch: SearchParams? = null

    companion object {
        private var instance: User? = null
        fun getCurrentUser(context: Context): User? {
            if (instance == null) {
                synchronized(User::class) {
                    instance = User(context)
                }
            }
            return instance
        }
    }

    //save radius and location of last search


    constructor(context: Context){
         prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    }

    fun setRadius(radius:Float){
        val editor = prefs!!.edit()
        editor.putFloat(PREFS_RADIUS, radius)
        editor.apply()
    }

    fun getRadius():Float{
        return prefs!!.getFloat(PREFS_RADIUS, 5f)
    }

    fun isLoggedIn():Boolean{
        return TwitterCore.getInstance().sessionManager.activeSession != null
    }

    fun setLastSearch(params:SearchParams){
        lastSearch = params
    }
    fun getLastSearch():SearchParams?{
        return lastSearch
    }
}