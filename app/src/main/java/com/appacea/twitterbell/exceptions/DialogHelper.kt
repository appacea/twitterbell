/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/7/19 3:40 AM
 *
 * Last modified 6/7/19 3:40 AM
 */

package com.appacea.twitterbell.exceptions

import android.content.Context
import androidx.appcompat.app.AlertDialog

class DialogHelper{


    companion object {
        fun showDialog(context: Context, title:String?, message:String?){
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}