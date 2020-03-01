package com.example.rflix.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rflix.R
import com.example.rflix.constant.Constants
import com.example.rflix.data.response.Movie
import com.example.rflix.data.response.MovieResponse
import com.example.rflix.data.response.ViewData
import com.example.rflix.presentation.activities.ShowAllActivity
import com.example.rflix.presentation.adapter.MovieAdapter
import com.example.rflix.presentation.viewmodels.MovieViewModel
import com.example.rflix.utils.OnBottomReachedListener
import com.example.rflix.utils.SharedData


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: MovieViewModel
    private var nowPlayingList: ArrayList<Movie> = ArrayList()
    private var popularList: ArrayList<Movie> = ArrayList()
    private var topRatedList: ArrayList<Movie> = ArrayList()
    var nowPlayingPage: Int = 1
    var popularPage: Int = 1
    var topRatedPage: Int = 1
    private lateinit var adapterNowPlaying: MovieAdapter
    private lateinit var adapterPopular: MovieAdapter
    private lateinit var adapterTopRated: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedData.backFrom = "home"
        homeViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        val mContext = container!!.context

        homeViewModel.movieStatus.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ViewData.Status.RUNNING -> Log.d("HomeFragment", "Running........")
                ViewData.Status.SUCCESS -> {

                    when (it.operationName) {
                        ViewData.Operation.NOW_PLAYING -> {
                            Log.e("NowPlaying $nowPlayingPage", "Get data from server successfully")

                            setNowPlayingOperation(it, rootView, mContext)
                        }
                        ViewData.Operation.POPULAR -> {
                            Log.e("Popular $popularPage", "Get data from server successfully")

                            setPopularOperation(it, rootView, mContext)
                        }
                        ViewData.Operation.TOP_RATED -> {
                            Log.e("TopRated $topRatedPage", "Get data from server successfully")

                            setTopRatedOperation(it, rootView, mContext)
                        }
                        else -> {
                            Log.d("Observer", "Empty Operation")
                        }
                    }
                }
                ViewData.Status.ERROR -> {
                    Log.d(
                        "HomeFragment",
                        "Fail to get data from server. Try again ----" + it.error?.message
                    )
                }
                else -> {
                    Log.d("Observer", "Empty Operation")
                }
            }
        })

        homeViewModel.getNowPlayingMovie(
            Constants.API_KEY,
            Constants.LANGUAGE,
            nowPlayingPage.toString()
        )



        return rootView
    }

    private fun setPopularOperation(
        it: ViewData<MovieResponse>,
        rootView: View,
        mContext: Context
    ) {
        popularList.addAll(it.value[0].results)
        val recyclerView =
            rootView.findViewById(R.id.popularList) as RecyclerView
        val title = rootView.findViewById(R.id.popularTitle) as TextView
        val btnPopular = rootView.findViewById(R.id.btnPopular) as ImageView
        title.visibility = View.VISIBLE
        btnPopular.visibility = View.VISIBLE

        if (popularPage == 1) {
            adapterPopular = MovieAdapter(popularList)
            val mLayoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(
                    mContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = adapterPopular

            homeViewModel.getTopRatedMovie(
                Constants.API_KEY,
                Constants.LANGUAGE,
                topRatedPage.toString()
            )
        } else {
            adapterPopular.notifyDataSetChanged()
        }

        adapterPopular.setOnBottomReachedCustomListener(object :
            OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                popularPage += 1
                homeViewModel.getPopularMovie(
                    Constants.API_KEY,
                    Constants.LANGUAGE,
                    popularPage.toString()
                )
            }
        })

        btnPopular.setOnClickListener {
            SharedData.detailsFragment = "popular"
            val intent = Intent(mContext, ShowAllActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setTopRatedOperation(
        it: ViewData<MovieResponse>,
        rootView: View,
        mContext: Context
    ) {
        topRatedList.addAll(it.value[0].results)
        val recyclerView =
            rootView.findViewById(R.id.topRatedList) as RecyclerView
        val title = rootView.findViewById(R.id.topRatedTitle) as TextView
        val btnTopRated = rootView.findViewById(R.id.btnTopRated) as ImageView
        title.visibility = View.VISIBLE
        btnTopRated.visibility = View.VISIBLE

        if (topRatedPage == 1) {
            adapterTopRated = MovieAdapter(topRatedList)
            val mLayoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(
                    mContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = adapterTopRated
        } else {
            adapterTopRated.notifyDataSetChanged()
        }

        adapterTopRated.setOnBottomReachedCustomListener(object :
            OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                topRatedPage += 1
                homeViewModel.getTopRatedMovie(
                    Constants.API_KEY,
                    Constants.LANGUAGE,
                    topRatedPage.toString()
                )
            }
        })

        btnTopRated.setOnClickListener {
            SharedData.detailsFragment = "topRated"
            val intent = Intent(mContext, ShowAllActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setNowPlayingOperation(
        it: ViewData<MovieResponse>,
        rootView: View,
        mContext: Context
    ) {
        nowPlayingList.addAll(it.value[0].results)
        val recyclerView =
            rootView.findViewById(R.id.nowPlayingList) as RecyclerView
        val title = rootView.findViewById(R.id.nowPlayingTitle) as TextView
        val btnNowPlaying = rootView.findViewById(R.id.btnNowPlaying) as ImageView
        title.visibility = View.VISIBLE
        btnNowPlaying.visibility = View.VISIBLE

        if (nowPlayingPage == 1) {
            SharedData.testMovieList = nowPlayingList
            adapterNowPlaying = MovieAdapter(nowPlayingList)
            val mLayoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(
                    mContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = adapterNowPlaying

            homeViewModel.getPopularMovie(
                Constants.API_KEY,
                Constants.LANGUAGE,
                popularPage.toString()
            )
        } else {
            adapterNowPlaying.notifyDataSetChanged()
        }

        adapterNowPlaying.setOnBottomReachedCustomListener(object :
            OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                nowPlayingPage += 1
                homeViewModel.getNowPlayingMovie(
                    Constants.API_KEY,
                    Constants.LANGUAGE,
                    nowPlayingPage.toString()
                )
            }
        })

        btnNowPlaying.setOnClickListener {
            SharedData.detailsFragment = "nowPlaying"
            val intent = Intent(mContext, ShowAllActivity::class.java)
            startActivity(intent)
        }
    }
}