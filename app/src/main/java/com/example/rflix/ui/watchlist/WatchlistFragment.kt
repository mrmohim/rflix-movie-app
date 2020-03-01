package com.example.rflix.ui.watchlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rflix.R
import com.example.rflix.data.response.Movie
import com.example.rflix.presentation.adapter.SingleMovieAdapter
import com.example.rflix.presentation.viewmodels.MovieViewModel
import com.example.rflix.utils.OnBottomReachedListener
import com.example.rflix.utils.SharedData

class WatchlistFragment : Fragment() {

    private lateinit var watchListViewModel: MovieViewModel
    private var watchList: ArrayList<Movie> = ArrayList()
    lateinit var adapterSingle: SingleMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedData.backFrom = "watchlist"
        watchListViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_watchlist, container, false)
        val mContext = container!!.context

        if (SharedData.userInfo?.watchList != null) {
            watchList.addAll(SharedData.userInfo!!.watchList)
            val recyclerView = rootView.findViewById(R.id.watchList) as RecyclerView

            adapterSingle = SingleMovieAdapter(watchList)
            val mLayoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(
                    mContext,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = adapterSingle

            adapterSingle.setOnBottomReachedCustomListener(object :
                OnBottomReachedListener {
                override fun onBottomReached(position: Int) {
                    Log.e("WatchListFragment", "List End!")
                }
            })
        }
        return rootView
    }
}