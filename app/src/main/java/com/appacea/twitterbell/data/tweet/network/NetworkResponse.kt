/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 3:49 PM
 *
 * Last modified 6/6/19 3:49 PM
 */

package com.appacea.twitterbell.data.tweet.network

class NetworkResponse<T>{

    val body: T?
    val message: String?

    val isFailure: Boolean


    constructor(error: Throwable) {
        this.body = null
        this.message = error.message
        this.isFailure = true
    }

    constructor(response: T) {
        this.message = null
        this.isFailure = false
        this.body = response
    }
}