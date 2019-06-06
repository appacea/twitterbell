/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 6:58 PM
 *
 * Last modified 6/5/19 6:58 PM
 */

package com.appacea.twitterbell.common.extensions

import com.google.android.gms.maps.model.Circle


fun Circle.getZoomLevel(): Float {
    var zoomLevel = 11f
    val radius = radius + radius / 2
    val scale = radius / 500
    zoomLevel = (16 - Math.log(scale) / Math.log(2.0)).toFloat()
    return zoomLevel
}