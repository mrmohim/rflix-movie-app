package com.example.rflix.presentation.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rflix.R
import com.example.rflix.constant.Constants
import com.example.rflix.data.response.*
import com.example.rflix.presentation.adapter.MovieAdapter
import com.example.rflix.presentation.viewmodels.MovieViewModel
import com.example.rflix.utils.FireUtils
import com.example.rflix.utils.OnBottomReachedListener
import com.example.rflix.utils.SharedData
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*
import kotlin.collections.ArrayList

class DetailActivity : AppCompatActivity() {

    private lateinit var videoViewModel: MovieViewModel
    private var videoList: ArrayList<Video> = ArrayList()
    private var castList: ArrayList<Cast> = ArrayList()
    private var crewList: ArrayList<Crew> = ArrayList()
    private var movieList: ArrayList<Movie> = ArrayList()
    var moviePage: Int = 1
    lateinit var adapterMovie: MovieAdapter
    var favoriteFlag = false
    var watchListFlag = false

    private lateinit var clickedMovie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        clickedMovie = SharedData.clickedMovie!!
        checkVipUser()

        videoViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = SharedData.clickedMovie!!.title

        setDetailViewObserver()
        setCreditsViewObserver()
        setMovieViewObserver()

        videoViewModel.getVideos(
            SharedData.clickedMovie!!.id,
            Constants.API_KEY,
            Constants.LANGUAGE
        )
    }

    private fun checkVipUser() {
        if (SharedData.uuid == "unknown") {
            favouriteImg.visibility = View.GONE
            watchListImg.visibility = View.GONE
        } else {
            checkFnWList()
        }
    }

    private fun checkFnWList() {
        SharedData.userInfo!!.favourite.forEach {
            if (it.original_title == clickedMovie.original_title) {
                favoriteFlag = true
                favouriteImg.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_favorite_black_24dp
                    )
                )
            }
        }

        SharedData.userInfo!!.watchList.forEach {
            if (it.original_title == clickedMovie.original_title) {
                watchListFlag = true
                watchListImg.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_playlist_add_check_black_24dp
                    )
                )
            }
        }
    }

    private fun setDetailViewObserver() {
        videoViewModel.videoStatus.observe(this, Observer {
            when (it.status) {
                ViewData.Status.RUNNING -> Log.d("DetailActivity", "Running........")
                ViewData.Status.SUCCESS -> {
                    Log.d("DetailActivity", "Get data from server successfully")
                    when (it.operationName) {
                        ViewData.Operation.VIDEO -> {
                            videoList.addAll(it.value[0].results)

                            var trailer = ""
                            var clip = ""
                            videoList.forEach { video ->
                                if (video.type.contains("Trailer") && trailer.isEmpty())
                                    trailer = video.key
                                else
                                    clip = video.key
                            }
                            if (trailer.isNotEmpty())
                                playVideo(trailer)
                            else
                                playVideo(clip)

                            val imageUrl =
                                "https://image.tmdb.org/t/p/w500" + clickedMovie.poster_path
                            Glide.with(this)
                                .load(imageUrl)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        movieProgressBar.visibility = View.GONE
                                        return false
                                    }
                                })
                                .centerCrop()
                                .into(movieImage)

                            movieTitle.text = clickedMovie.title
                            ratingBar.rating = ((clickedMovie.vote_average / 10.0) * 5.0).toFloat()
                            movieRelease.text =
                                if (clickedMovie.release_date.isNotEmpty()) clickedMovie.release_date.substring(
                                    0,
                                    4
                                ) else "Unknown"
                            movieRating.text = clickedMovie.vote_average.toString()
                            val langText =
                                " ${clickedMovie.original_language.toUpperCase(Locale.getDefault())} "
                            langTitle.text = langText

                            favouriteImg.setOnClickListener {
                                favouriteListener()
                            }

                            watchListImg.setOnClickListener {
                                watchListListener()
                            }

                            videoViewModel.getCredits(
                                clickedMovie.id,
                                Constants.API_KEY
                            )
                        }
                        else -> {
                            Log.d("Observer", "Empty Operation")
                        }
                    }
                }
                ViewData.Status.ERROR -> {
                    Log.d(
                        "DetailActivity",
                        "Fail to get data from server. Try again ----" + it.error?.message
                    )
                }
                else -> {
                    Log.d("Observer", "Empty Operation")
                }
            }
        })
    }

    private fun watchListListener() {
        if (!watchListFlag) {
            watchListImg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_playlist_add_check_black_24dp
                )
            )
            SharedData.userInfo!!.watchList.add(clickedMovie)
        } else {
            watchListImg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_playlist_add_black_24dp
                )
            )
            var selectedMovie = 0
            SharedData.userInfo!!.watchList.forEachIndexed { index, movie ->
                if (movie == clickedMovie) {
                    selectedMovie = index
                }
            }
            SharedData.userInfo!!.watchList.removeAt(selectedMovie)
        }
        watchListFlag = !watchListFlag
    }

    private fun favouriteListener() {
        if (!favoriteFlag) {
            favouriteImg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_favorite_black_24dp
                )
            )
            SharedData.userInfo!!.favourite.add(clickedMovie)
        } else {
            favouriteImg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_favorite_border_black_24dp
                )
            )
            var selectedMovie = 0
            SharedData.userInfo!!.favourite.forEachIndexed { index, movie ->
                if (movie == clickedMovie) {
                    selectedMovie = index
                }
            }
            SharedData.userInfo!!.favourite.removeAt(selectedMovie)
        }
        favoriteFlag = !favoriteFlag
    }

    private fun setCreditsViewObserver() {
        videoViewModel.creditsStatus.observe(this, Observer {
            when (it.status) {
                ViewData.Status.RUNNING -> Log.d("DetailActivity", "Running........")
                ViewData.Status.SUCCESS -> {
                    Log.d("DetailActivity", "Get data from server successfully")
                    when (it.operationName) {
                        ViewData.Operation.CREDITS -> {
                            originalTitle.visibility = View.VISIBLE
                            originalTitle.text =
                                Html.fromHtml("<b>Original title:</b> " + SharedData.clickedMovie!!.original_title)

                            castList.addAll(it.value[0].cast)
                            crewList.addAll(it.value[0].crew)
                            var castStr = "<b>Starring:</b> "
                            for (i in 0 until castList.size) {
                                if (i > 5)
                                    break
                                if (i in 1..5 && i < (castList.size - 1))
                                    castStr += ", "
                                castStr += castList[i].name
                            }
                            starringText.visibility = View.VISIBLE
                            starringText.text = Html.fromHtml(castStr)

                            var crewStr = "<b>Directed by:</b> "
                            for (i in 0 until crewList.size) {
                                if (crewList[i].job == "Director") {
                                    crewStr += crewList[i].name
                                    break
                                }
                            }

                            directedText.visibility = View.VISIBLE
                            directedText.text = Html.fromHtml(crewStr)

                            overviewText.visibility = View.VISIBLE
                            overviewText.text =
                                Html.fromHtml("<b>Overview:</b> " + SharedData.clickedMovie!!.overview)

                            videoViewModel.getSimilarMovie(
                                SharedData.clickedMovie!!.id,
                                Constants.API_KEY,
                                Constants.LANGUAGE,
                                moviePage.toString()
                            )
                        }
                        else -> {
                            Log.d("Observer", "Empty Operation")
                        }
                    }
                }
                ViewData.Status.ERROR -> {
                    Log.d(
                        "DetailActivity",
                        "Fail to get data from server. Try again ----" + it.error?.message
                    )
                }
                else -> {
                    Log.d("Observer", "Empty Operation")
                }
            }
        })
    }

    private fun setMovieViewObserver() {
        videoViewModel.movieStatus.observe(this, Observer {
            when (it.status) {
                ViewData.Status.RUNNING -> Log.d("DetailActivity", "Running........")
                ViewData.Status.SUCCESS -> {
                    Log.d("DetailActivity", "Get data from server successfully")
                    when (it.operationName) {
                        ViewData.Operation.SIMILAR -> {
                            movieList.addAll(it.value[0].results)

                            if (movieList.isNotEmpty()) {
                                similarTitle.visibility = View.VISIBLE
                                val recyclerView = findViewById<RecyclerView>(R.id.similarList)

                                if (moviePage == 1) {
                                    adapterMovie = MovieAdapter(movieList)
                                    val mLayoutManager: RecyclerView.LayoutManager =
                                        LinearLayoutManager(
                                            this,
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                        )
                                    recyclerView.layoutManager = mLayoutManager
                                    recyclerView.adapter = adapterMovie
                                } else {
                                    adapterMovie.notifyDataSetChanged()
                                }

                                adapterMovie.setOnBottomReachedCustomListener(object :
                                    OnBottomReachedListener {
                                    override fun onBottomReached(position: Int) {
                                        moviePage += 1
                                        videoViewModel.getSimilarMovie(
                                            SharedData.clickedMovie!!.id,
                                            Constants.API_KEY,
                                            Constants.LANGUAGE,
                                            moviePage.toString()
                                        )
                                    }
                                })
                            }
                        }
                        else -> {
                            Log.d("Observer", "Empty Operation")
                        }
                    }
                }
                ViewData.Status.ERROR -> {
                    Log.d(
                        "DetailActivity",
                        "Fail to get data from server. Try again ----" + it.error?.message
                    )
                }
                else -> {
                    Log.d("Observer", "Empty Operation")
                }
            }
        })
    }

    private fun playVideo(videoLink: String) {
        val youTubePlayerFragment =
            supportFragmentManager.findFragmentById(R.id.youtubePlayerView) as YouTubePlayerSupportFragment

        youTubePlayerFragment.initialize(
            Constants.YOUTUBE_KEY,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    youTubePlayer: YouTubePlayer,
                    wasRestored: Boolean
                ) {
                    if (!wasRestored) {
                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
                        youTubePlayer.cueVideo(videoLink)
                    }
                }

                override fun onInitializationFailure(
                    arg0: YouTubePlayer.Provider,
                    arg1: YouTubeInitializationResult
                ) {
                    Log.e("DetailActivity", "Youtube Player View initialization failed")
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (SharedData.uuid != "unknown") {
            FireUtils.updateUserInfo(SharedData.uuid, SharedData.userInfo!!)
        }
        val intent = Intent(this@DetailActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}
