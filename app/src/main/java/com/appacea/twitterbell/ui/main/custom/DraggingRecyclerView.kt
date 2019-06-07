/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 12:15 PM
 *
 * Last modified 6/5/19 12:15 PM
 */

package com.appacea.twitterbell.ui.main.custom

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.appacea.twitterbell.R
import com.appacea.twitterbell.common.extensions.dpToPx


enum class DraggingRecyclerViewMode(val value: Int) {
    DOWN(1),
    DRAGGING(2),
    UP(3),
    ANIMATING(4)
}

private const val BOTTOM_HEIGHT = 100 //dp

class DraggingRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnTouchListener, GestureDetector.OnGestureListener {

    private var isOpen:Boolean = true
    private var mMode: Int = 0
    private var recyclerView: RecyclerView
    private var flBackground: FrameLayout
    private var mDetector: GestureDetectorCompat
    private lateinit var mAnimatorSet: AnimatorSet
    private var listener: DraggingRecyclerViewListener? = null
    init{
        LayoutInflater.from(context).inflate(R.layout.view_draggingrecyclerview, this, true)
        this.recyclerView = findViewById(R.id.rvDraggable)
        this.recyclerView.setOnTouchListener(this)
        this.flBackground = findViewById(R.id.flBackground)
        this.mDetector = GestureDetectorCompat(context, this)

    }

    fun getRecyclerView(): RecyclerView {
        return this.recyclerView
    }

    fun setListener(listener:DraggingRecyclerViewListener){
        this.listener = listener
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if(!isOpen){
            if (mDetector.onTouchEvent(event)) {
                return true
            }
            else{
                return super.onTouchEvent(event)
            }
        }
        else return mDetector.onTouchEvent(event)

    }

    fun close(){

        mMode = DraggingRecyclerViewMode.ANIMATING.value
        var objectAnimatorTranslation = ObjectAnimator.ofFloat(this.recyclerView, "translationY", this.recyclerView.getY(), this.height-BOTTOM_HEIGHT.dpToPx())
        var bjectAnimatorAlpha = ObjectAnimator.ofFloat(this.flBackground, "alpha", this.flBackground.alpha, 0f )

        mAnimatorSet = AnimatorSet()
        mAnimatorSet.playTogether(objectAnimatorTranslation, bjectAnimatorAlpha)
        mAnimatorSet.duration = 200
        mAnimatorSet.addListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                mMode = DraggingRecyclerViewMode.DOWN.value
                isOpen = false
            }
        })
        mAnimatorSet.start()
        listener?.onClose()
    }

    fun open(){

        var objectAnimatorTranslation = ObjectAnimator.ofFloat(this.recyclerView, "translationY", this.recyclerView.getY(), 0f )
        var bjectAnimatorAlpha = ObjectAnimator.ofFloat(this.flBackground, "alpha", this.flBackground.alpha, 1f )

        mAnimatorSet = AnimatorSet()
        mAnimatorSet.playTogether(objectAnimatorTranslation, bjectAnimatorAlpha)
        mAnimatorSet.duration = 200
        mAnimatorSet.addListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                mMode = DraggingRecyclerViewMode.UP.value
                isOpen = true
            }
        })
        mAnimatorSet.start()
        listener?.onOpen()
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if(Math.abs(velocityY)>Math.abs(velocityX)){
            if(isOpen){
                close()
            }
            else{
                open()
            }
        }

        return !isOpen
    }


    override fun onShowPress(p0: MotionEvent?) {
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return !isOpen
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return !isOpen
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return !isOpen
    }

    override fun onLongPress(p0: MotionEvent?) {
    }
}

interface DraggingRecyclerViewListener{
    fun onClose()
    fun onOpen()
}