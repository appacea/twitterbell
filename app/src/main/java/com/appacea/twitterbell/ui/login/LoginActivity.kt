/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/6/19 10:15 PM
 *
 * Last modified 6/6/19 10:15 PM
 */

package com.appacea.twitterbell.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.appacea.twitterbell.R
import com.appacea.twitterbell.data.user.User
import com.appacea.twitterbell.exceptions.ErrorHandling
import com.appacea.twitterbell.ui.main.MapsActivity
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton


class LoginActivity : AppCompatActivity() {


    lateinit var etUsername: EditText
    lateinit var etPassword: EditText
    lateinit var tlbLogin: TwitterLoginButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Twitter.initialize(this)

        setContentView(R.layout.activity_login)


        val user = User.getCurrentUser(this)
        if(user?.isLoggedIn()!!){
            openMapsActivity()
        }

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        tlbLogin = findViewById(R.id.tlbLogin)

        // Create a callback that’ll handle the results of the login attempts//
        tlbLogin.setCallback(object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                openMapsActivity()
            }

            override fun failure(exception: TwitterException?) {
                ErrorHandling.showErrorDialog(this@LoginActivity,exception?.message)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.tlbLogin.onActivityResult(requestCode, resultCode, data)
    }

    fun openMapsActivity(){
        val intent = Intent(this@LoginActivity, MapsActivity::class.java)
        startActivity(intent)
    }
}