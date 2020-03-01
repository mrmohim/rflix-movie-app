package com.example.rflix.data.response

class Movie {
    var popularity: Float = 0F
    var vote_count: Int = 0
    var video: Boolean = false
    var poster_path: String = ""
    var id: Int = 0
    var adult: Boolean = false
    var backdrop_path: String = ""
    var original_language: String = ""
    var original_title: String = ""
    var title: String = ""
    var vote_average: Float = 0F
    var overview: String = ""
    var release_date: String = ""

    override fun toString(): String {
        return "$popularity, $vote_count, $video, $poster_path, $id, $adult, $backdrop_path, $original_language, $original_title, $title, $vote_average, $overview, $release_date"
    }
}