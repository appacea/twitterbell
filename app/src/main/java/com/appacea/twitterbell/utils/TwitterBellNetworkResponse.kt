/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/4/19 8:41 PM
 *
 * Last modified 6/4/19 8:41 PM
 */

package com.appacea.twitterbell.utils

import com.appacea.twitterbell.exceptions.TwitterBellNetworkError

interface TwitterBellNetworkResponse<T>{

    fun onResponse(response: T)

    fun onError(error: TwitterBellNetworkError)
}
