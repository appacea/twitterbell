/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 5:18 PM
 *
 * Last modified 6/6/19 5:18 PM
 */

package com.appacea.twitterbell.data.tweet.entities

import com.appacea.twitterbell.data.tweet.network.TweetMedia

data class TweetEntity(val media:List<TweetMedia>)