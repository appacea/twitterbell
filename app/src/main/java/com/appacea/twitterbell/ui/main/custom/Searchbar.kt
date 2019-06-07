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
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import com.appacea.twitterbell.R

class Searchbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var searchView: SearchView

    init{
        LayoutInflater.from(context).inflate(R.layout.view_searchbar, this, true)
        searchView = findViewById(R.id.searchView)


    }


    fun setListener(listener:SearchbarListener){
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                listener.onSearch(query)
                searchView.clearFocus();
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }


}

interface SearchbarListener{
    fun onSearch(query: String?)
}