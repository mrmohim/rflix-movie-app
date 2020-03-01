package com.example.rflix.data.repository

import com.example.rflix.constant.Config
import com.example.rflix.data.remote.ApiProvider
import com.example.rflix.data.remote.MovieApi
import com.example.rflix.data.response.CreditsResponse
import com.example.rflix.data.response.MovieResponse
import com.example.rflix.data.response.VideoResponse
import io.reactivex.Observable
import retrofit2.Response

class MovieRepository {
    private val movieApi = ApiProvider.provideApi(Config.MOVIE_URL).create(MovieApi::class.java)

    fun getTopRatedMovie(apiKey: String, language: String, page: String): Observable<Response<MovieResponse>> {
        return movieApi.getTopRatedMovie(apiKey, language, page)
    }

    fun getUpcomingMovie(apiKey: String, language: String, page: String): Observable<Response<MovieResponse>> {
        return movieApi.getUpcomingMovie(apiKey, language, page)
    }

    fun getPopularMovie(apiKey: String, language: String, page: String): Observable<Response<MovieResponse>> {
        return movieApi.getPopularMovie(apiKey, language, page)
    }

    fun getNowPlayingMovie(apiKey: String, language: String, page: String): Observable<Response<MovieResponse>> {
        return movieApi.getNowPlayingMovie(apiKey, language, page)
    }

    fun getSimilarMovie(movie_id: Int, apiKey: String, language: String, page: String): Observable<Response<MovieResponse>> {
        return movieApi.getSimilarMovie(movie_id, apiKey, language, page)
    }

    fun getSearch(apiKey: String, language: String, query: String, page: String, includeAdult: String): Observable<Response<MovieResponse>> {
        return movieApi.getSearch(apiKey, language, query, page, includeAdult)
    }

    fun getVideos(movie_id: Int, apiKey: String, language: String): Observable<Response<VideoResponse>> {
        return movieApi.getVideos(movie_id, apiKey, language)
    }

    fun getCredits(movie_id: Int, apiKey: String): Observable<Response<CreditsResponse>> {
        return movieApi.getCredits(movie_id, apiKey)
    }
}