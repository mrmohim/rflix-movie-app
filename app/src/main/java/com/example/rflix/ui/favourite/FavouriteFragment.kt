package com.example.rflix.ui.favourite

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

class FavouriteFragment : Fragment() {

    private lateinit var favouriteViewModel: MovieViewModel
    private var favouriteList: ArrayList<Movie> = ArrayList()
    lateinit var adapterSingle: SingleMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedData.backFrom = "favourite"
        favouriteViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_favourite, container, false)
        val mContext = container!!.context

        if (SharedData.userInfo?.favourite != null) {
            favouriteList.addAll(SharedData.userInfo!!.favourite)
            val recyclerView = rootView.findViewById(R.id.favouriteList) as RecyclerView

            adapterSingle = SingleMovieAdapter(favouriteList)
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
                    Log.e("FavouriteFragment", "List End!")
                }
            })
        }
        return rootView
    }
}