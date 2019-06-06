/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 12:43 PM
 *
 * Last modified 6/5/19 12:43 PM
 */

package com.appacea.twitterbell.common.extensions

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.annotation.Dimension


@JvmOverloads @Dimension(unit = Dimension.PX) fun Number.dpToPx(
    metrics: DisplayMetrics = Resources.getSystem().displayMetrics
): Float {
    return toFloat() * metrics.density
}

@JvmOverloads @Dimension(unit = Dimension.DP) fun Number.pxToDp(
    metrics: DisplayMetrics = Resources.getSystem().displayMetrics
): Float {
    return toFloat() / metrics.density
}
