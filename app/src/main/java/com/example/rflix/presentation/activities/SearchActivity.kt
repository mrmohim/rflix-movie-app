package com.example.rflix.presentation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rflix.R
import com.example.rflix.constant.Constants
import com.example.rflix.data.response.Movie
import com.example.rflix.data.response.ViewData
import com.example.rflix.presentation.adapter.MovieAdapter
import com.example.rflix.presentation.viewmodels.MovieViewModel
import com.example.rflix.utils.OnBottomReachedListener
import com.example.rflix.utils.SharedData

class SearchActivity : AppCompatActivity() {

    private lateinit var searchViewModel: MovieViewModel
    private var searchList: ArrayList<Movie> = ArrayList()
    var searchPage: Int = 1
    lateinit var adapterMovie: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Search Results of \"${SharedData.searchContent}\""

        setSearchViewObserver()

        searchViewModel.getSearch(
            Constants.API_KEY,
            Constants.LANGUAGE,
            SharedData.searchContent,
            searchPage.toString(),
            "false"
        )
    }

    private fun setSearchViewObserver() {
        searchViewModel.movieStatus.observe(this, Observer {
            when (it.status) {
                ViewData.Status.RUNNING -> Log.d("SearchActivity", "Running........")
                ViewData.Status.SUCCESS -> {
                    Log.d("SearchActivity", "Get data from server successfully")
                    when (it.operationName) {
                        ViewData.Operation.SEARCH -> {
                            searchList.addAll(it.value[0].results)
                            val recyclerView = findViewById<RecyclerView>(R.id.searchList)
                            val noResultFound = findViewById<TextView>(R.id.noResultFound)
                            if(searchList.size != 0) {
                                if (searchPage == 1) {
                                    adapterMovie = MovieAdapter(searchList)
                                    val mLayoutManager: RecyclerView.LayoutManager =
                                        GridLayoutManager(this, 3)
                                    recyclerView.layoutManager = mLayoutManager
                                    recyclerView.adapter = adapterMovie
                                } else {
                                    adapterMovie.notifyDataSetChanged()
                                }

                                adapterMovie.setOnBottomReachedCustomListener(object :
                                    OnBottomReachedListener {
                                    override fun onBottomReached(position: Int) {
                                        searchPage += 1
                                        searchViewModel.getSearch(
                                            Constants.API_KEY,
                                            Constants.LANGUAGE,
                                            SharedData.searchContent,
                                            searchPage.toString(),
                                            "false"
                                        )
                                    }
                                })
                            } else {
                                recyclerView.visibility = View.GONE
                                noResultFound.visibility = View.VISIBLE
                            }
                        }
                        else -> {
                            Log.d("Observer", "Empty Operation")
                        }
                    }
                }
                ViewData.Status.ERROR -> {
                    Log.d(
                        "UpcomingFragment",
                        "Fail to get data from server. Try again ----" + it.error?.message
                    )
                }
                else -> {
                    Log.d("Observer", "Empty Operation")
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SharedData.backFrom = "login"
        val intent = Intent(this@SearchActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}
