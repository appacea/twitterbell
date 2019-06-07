/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/7/19 2:17 AM
 *
 * Last modified 6/7/19 2:17 AM
 */

package com.appacea.twitterbell.exceptions

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.appacea.twitterbell.R

class ErrorHandling{


    companion object {
        val TAG = "ErrorHandling"
        fun showErrorDialog(context: Context, error:String?){
            val message = if(error == null) context.getString(R.string.default_error) else error
            Log.e(TAG,message)
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.default_error_title))
            builder.setMessage(message)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}