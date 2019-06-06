/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 4:13 PM
 *
 * Last modified 6/5/19 4:13 PM
 */

package com.appacea.twitterbell.common.extensions

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.appacea.twitterbell.ui.main.custom.OnSnapPositionChangeListener
import com.appacea.twitterbell.ui.main.custom.SnapOnScrollListener

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onSnapPositionChangeListener: OnSnapPositionChangeListener
) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper,  behavior, onSnapPositionChangeListener)
    addOnScrollListener(snapOnScrollListener)
}