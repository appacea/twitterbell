/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 6:40 PM
 *
 * Last modified 6/5/19 6:40 PM
 */

package com.appacea.twitterbell.common.extensions

import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polygon

fun Polygon.getBounds():LatLngBounds{
    val builder = LatLngBounds.builder()
    for(point in this.points){
        builder.include(point)
    }
    return builder.build()
}