/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 5:21 PM
 *
 * Last modified 6/6/19 5:21 PM
 */

package com.appacea.twitterbell.data.tweet.entities

import com.appacea.twitterbell.data.tweet.network.TweetMediaVideoVariant

data class TweetMediaVideo(val variants:List<TweetMediaVideoVariant>)