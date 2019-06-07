/*
 * Copyright (c) Tchipr Ltd 2019. All right reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Yvan Stern on 6/5/19 12:15 PM
 *
 * Last modified 6/5/19 12:15 PM
 */

package com.appacea.twitterbell.ui.main.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import com.appacea.twitterbell.R

class Searchbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var searchView: SearchView
    private var menuButton: ImageButton
    init{
        LayoutInflater.from(context).inflate(R.layout.view_searchbar, this, true)
        searchView = findViewById(R.id.searchView)
        menuButton = findViewById(R.id.menuButton)

    }


    fun setListener(listener:SearchbarListener){
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                listener.onSearch(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        menuButton.setOnClickListener(object:OnClickListener{
            override fun onClick(p0: View?) {
                listener.onMenuClicked()
            }

        })
    }


}

interface SearchbarListener{
    fun onSearch(query: String?)
    fun onMenuClicked()
}