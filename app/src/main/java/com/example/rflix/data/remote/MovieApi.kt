package com.example.rflix.data.remote

import com.example.rflix.data.response.CreditsResponse
import com.example.rflix.data.response.MovieResponse
import com.example.rflix.data.response.VideoResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @Headers("Content-Type: application/json")
    @GET("movie/top_rated")
    fun getTopRatedMovie(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: String
    ): Observable<Response<MovieResponse>>

    @Headers("Content-Type: application/json")
    @GET("movie/upcoming")
    fun getUpcomingMovie(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: String
    ): Observable<Response<MovieResponse>>

    @Headers("Content-Type: application/json")
    @GET("movie/popular")
    fun getPopularMovie(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: String
    ): Observable<Response<MovieResponse>>


    @Headers("Content-Type: application/json")
    @GET("movie/now_playing")
    fun getNowPlayingMovie(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: String
    ): Observable<Response<MovieResponse>>

    @Headers("Content-Type: application/json")
    @GET("movie/{movie_id}/similar")
    fun getSimilarMovie(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: String
    ): Observable<Response<MovieResponse>>

    @Headers("Content-Type: application/json")
    @GET("search/movie")
    fun getSearch(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: String,
        @Query("include_adult") include_adult: String
    ): Observable<Response<MovieResponse>>

    @Headers("Content-Type: application/json")
    @GET("movie/{movie_id}/videos")
    fun getVideos(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String,
        @Query("language") language: String
    ): Observable<Response<VideoResponse>>

    @Headers("Content-Type: application/json")
    @GET("movie/{movie_id}/credits")
    fun getCredits(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String
    ): Observable<Response<CreditsResponse>>
}