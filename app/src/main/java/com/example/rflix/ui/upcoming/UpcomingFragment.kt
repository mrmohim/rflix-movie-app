package com.example.rflix.ui.upcoming

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.rflix.presentation.adapter.SingleMovieAdapter
import com.example.rflix.presentation.viewmodels.MovieViewModel
import com.example.rflix.utils.OnBottomReachedListener
import com.example.rflix.utils.SharedData

class UpcomingFragment : Fragment() {

    private lateinit var upcomingViewModel: MovieViewModel
    private var upcomingList: ArrayList<Movie> = ArrayList()
    var upcomingPage: Int = 1
    lateinit var adapterSingle: SingleMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedData.backFrom = "upcoming"
        upcomingViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_upcoming, container, false)
        val mContext = container!!.context

        upcomingViewModel.movieStatus.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ViewData.Status.RUNNING -> Log.d("UpcomingFragment", "Running........")
                ViewData.Status.SUCCESS -> {
                    Log.d("UpcomingFragment", "Get data from server successfully")

                    when (it.operationName) {
                        ViewData.Operation.UPCOMING -> {
                            setUpcomingOperation(it, rootView, mContext)
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

        upcomingViewModel.getUpcomingMovie(
            Constants.API_KEY,
            Constants.LANGUAGE,
            upcomingPage.toString()
        )

        return rootView
    }

    private fun setUpcomingOperation(
        it: ViewData<MovieResponse>,
        rootView: View,
        mContext: Context
    ) {
        upcomingList.addAll(it.value[0].results)
        val recyclerView = rootView.findViewById(R.id.upcomingList) as RecyclerView

        if (upcomingPage == 1) {
            adapterSingle = SingleMovieAdapter(upcomingList)
            val mLayoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(
                    mContext,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = adapterSingle
        } else {
            adapterSingle.notifyDataSetChanged()
        }

        adapterSingle.setOnBottomReachedCustomListener(object :
            OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                upcomingPage += 1
                upcomingViewModel.getUpcomingMovie(
                    Constants.API_KEY,
                    Constants.LANGUAGE,
                    upcomingPage.toString()
                )
            }
        })
    }
}