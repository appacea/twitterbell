/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 7:20 PM
 *
 * Last modified 6/6/19 7:20 PM
 */

package com.appacea.twitterbell.ui.main.custom

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import com.appacea.twitterbell.R
import androidx.core.graphics.drawable.DrawableCompat




class MediaThumbnailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var imageView: ImageView
    var imageButton: ImageButton
    val drawable : Drawable?

    init{
        LayoutInflater.from(context).inflate(R.layout.view_mediathumbnail, this, true)
        imageView = findViewById(R.id.ivMediaThumbnail)
        imageButton = findViewById(R.id.ibMediaThumbnail)
        val unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_play_circle_outline_24px)
        if(unwrappedDrawable!=null){
            drawable = DrawableCompat.wrap(unwrappedDrawable)
            DrawableCompat.setTint(drawable, Color.WHITE)
        }else drawable = null
    }


    fun showPlay(){
        imageButton.setImageDrawable(drawable)
    }

    fun hidePlay(){
        imageButton.setImageDrawable(null)
    }


}