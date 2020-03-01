package com.example.rflix.utils

import androidx.annotation.Keep
import com.example.rflix.data.response.Movie
import com.google.firebase.database.IgnoreExtraProperties


object SharedData {
    var detailsFragment: String = ""
    var searchContent: String = ""
    var clickedMovie: Movie? = null
    var testMovieList: ArrayList<Movie> = ArrayList()
    var uuid: String = ""
    var userInfo: User? = null
    var tempUserInfo: User? = null
    var splashFlag = true
    var backFrom = "main"
    var feedback: Feedback? = null
}

@Keep
@IgnoreExtraProperties
open class User(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var imgUrl: String? = null,
    var favourite: ArrayList<Movie> = ArrayList(),
    var watchList: ArrayList<Movie> = ArrayList())

@Keep
@IgnoreExtraProperties
open class Feedback(
    var uid: String? = null,
    var email: String? = null,
    var messege: String? = null)