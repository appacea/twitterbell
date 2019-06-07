/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 7:19 PM
 *
 * Last modified 6/6/19 7:19 PM
 */

package com.appacea.twitterbell.ui.main.custom

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.VideoView
import com.appacea.twitterbell.R
import com.appacea.twitterbell.data.tweet.entities.TweetMediaType
import com.appacea.twitterbell.data.tweet.network.TweetMedia
import com.squareup.picasso.Picasso

class MediaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val imageView: ImageView
    val videoView: VideoView

    init{
        LayoutInflater.from(context).inflate(R.layout.view_media, this, true)
        imageView = findViewById(R.id.ivMedia)
        videoView = findViewById(R.id.vvMedia)
        imageView.visibility = View.GONE
        videoView.visibility = View.GONE
    }

    fun setPhoto(url:String){
        videoView.visibility = View.GONE
        imageView.visibility = View.VISIBLE
        Picasso.get().load(url).into(imageView)
    }

    fun setVideo(url:String){
        videoView.visibility = View.VISIBLE
        imageView.visibility = View.GONE
        videoView.setVideoURI(Uri.parse(url))
        videoView.start()
    }


    fun setMedia(media: TweetMedia){
        if(media.type == TweetMediaType.video.name){
            setVideo(media.video_info.variants.get(0).url)
        }
        else{
            setPhoto(media.media_url)
        }
    }
}