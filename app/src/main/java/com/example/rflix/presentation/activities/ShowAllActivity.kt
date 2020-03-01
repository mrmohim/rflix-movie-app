package com.example.rflix.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class ShowAllActivity : AppCompatActivity() {
    private lateinit var detailsViewModel: MovieViewModel
    private var detailsList: ArrayList<Movie> = ArrayList()
    var detailsPage: Int = 1
    lateinit var adapterMovie: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all)

        detailsViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setDetailsViewObserver()

        when (SharedData.detailsFragment) {
            "popular" -> {
                supportActionBar!!.title = resources.getString(R.string.most_popular_movies)
                detailsViewModel.getPopularMovie(
                    Constants.API_KEY,
                    Constants.LANGUAGE,
                    detailsPage.toString()
                )
            }
            "nowPlaying" -> {
                supportActionBar!!.title = resources.getString(R.string.now_playing_movies)
                detailsViewModel.getNowPlayingMovie(
                    Constants.API_KEY,
                    Constants.LANGUAGE,
                    detailsPage.toString()
                )
            }
            else -> {
                supportActionBar!!.title = resources.getString(R.string.top_rated_movies)
                detailsViewModel.getTopRatedMovie(
                    Constants.API_KEY,
                    Constants.LANGUAGE,
                    detailsPage.toString()
                )
            }
        }
    }

    private fun setDetailsViewObserver() {
        detailsViewModel.movieStatus.observe(this, Observer {
            when (it.status) {
                ViewData.Status.RUNNING -> Log.d("DetailsActivity", "Running........")
                ViewData.Status.SUCCESS -> {
                    Log.d("DetailsActivity", "Get data from server successfully")

                    if (it.operationName == ViewData.Operation.POPULAR || it.operationName == ViewData.Operation.TOP_RATED || it.operationName == ViewData.Operation.NOW_PLAYING) {
                        detailsList.addAll(it.value[0].results)
                        val recyclerView = findViewById<RecyclerView>(R.id.searchList)

                        if (detailsPage == 1) {
                            adapterMovie = MovieAdapter(detailsList)
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
                                detailsPage += 1
                                when (SharedData.detailsFragment) {
                                    "popular" -> {
                                        detailsViewModel.getPopularMovie(
                                            Constants.API_KEY,
                                            Constants.LANGUAGE,
                                            detailsPage.toString()
                                        )
                                    }
                                    "nowPlaying" -> {
                                        detailsViewModel.getNowPlayingMovie(
                                            Constants.API_KEY,
                                            Constants.LANGUAGE,
                                            detailsPage.toString()
                                        )
                                    }
                                    else -> {
                                        detailsViewModel.getTopRatedMovie(
                                            Constants.API_KEY,
                                            Constants.LANGUAGE,
                                            detailsPage.toString()
                                        )
                                    }
                                }
                            }
                        })
                    } else {
                        Log.d("Observer", "Empty Operation")
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
}
