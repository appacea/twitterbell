/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 3:04 PM
 *
 * Last modified 6/5/19 3:04 PM
 */

package com.appacea.twitterbell.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appacea.twitterbell.R
import com.appacea.twitterbell.data.tweet.entities.Tweet
import com.appacea.twitterbell.data.tweet.entities.TweetMediaType
import com.appacea.twitterbell.data.tweet.network.TweetMedia
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_draggingrecyclerview_item.view.*


/**
 * Adapter to show tweets
 *
 */
class TweetsAdapter(val items : List<Tweet>, val context: Context, val listener: TweetsAdapterListener) : RecyclerView.Adapter<TweetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        return TweetViewHolder(LayoutInflater.from(context).inflate(R.layout.view_draggingrecyclerview_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Bind the tweet to view
     *
     * Each tweet can show up to 3 images/videos in our app
     */
    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        holder.tvName.text = items.get(position).user?.name
        holder.tvScreenName.text ="@"+items.get(position).user?.screen_name
        holder.tvText.text = items.get(position).text

        Picasso.get().load(items.get(position).user?.profile_image_url).into(holder.ivProfile)

        //Loop through imageviews and set photo or video if exists
        val media = items.get(position).entities?.media
        for (i in holder.media.indices) { //set up imageviews
            if(media != null && media.size>=i+1){ //if we have a media for the view then set it, otherwise hide it

                val tweetMedia = media.get(i)
                holder.media[i].visibility = View.VISIBLE
                Picasso.get().load(tweetMedia.media_url).into(holder.media[i].imageView)
                holder.media[i].imageButton.setOnClickListener(object:View.OnClickListener{
                    override fun onClick(view: View?) {
                        listener.onPhotoClicked(view,tweetMedia)
                    }

                })

                if(tweetMedia.type == TweetMediaType.video.name){
                    holder.media[i].showPlay()
                }else{
                    holder.media[i].hidePlay()
                }
            }
            else{
                holder.media[i].visibility = View.GONE
            }
        }


        holder.bRetweet.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                listener.onRetweetClicked(getTweet(position))
            }
        })

        holder.bFavorite.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                listener.onFavoriteClicked(getTweet(position))
            }
        })
    }

    /**
     * Return the tweet by position
     */
    fun getTweet(position:Int): Tweet?{
        if(position>items.size){
            return null
        }
        return items.get(position)
    }
}

interface TweetsAdapterListener {
    fun onPhotoClicked(view: View?, media:TweetMedia?)
    fun onRetweetClicked(tweet:Tweet?)
    fun onFavoriteClicked(tweet:Tweet?)
}

class TweetViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val ivProfile = view.ivProfile
    val tvText = view.tvText
    val tvScreenName = view.tvScreenName
    val media = listOf(view.mtv1,view.mtv2,view.mtv3)
    val bRetweet = view.bRetweet
    var bFavorite = view.bFavorite

}