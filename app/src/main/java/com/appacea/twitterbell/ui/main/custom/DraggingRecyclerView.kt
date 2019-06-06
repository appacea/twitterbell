/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 12:15 PM
 *
 * Last modified 6/5/19 12:15 PM
 */

package com.appacea.twitterbell.ui.main.custom

import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.appacea.twitterbell.R
import com.appacea.twitterbell.common.extensions.attachSnapHelperWithListener

class DraggingRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private var dX: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()
    private var lastAction: Int = 0
    private var mMode: Int = 0
    private var recyclerView: RecyclerView
    private var flBackground: FrameLayout
    private var mIsLocked: Boolean = false
    private lateinit var mAnimatorSet: AnimatorSet

    init{
        LayoutInflater.from(context).inflate(R.layout.view_draggingrecyclerview, this, true)
        this.recyclerView = findViewById(R.id.rvDraggable)
        this.flBackground = findViewById(R.id.flBackground)

    }

    fun getRecyclerView(): RecyclerView {
        return this.recyclerView
    }

}