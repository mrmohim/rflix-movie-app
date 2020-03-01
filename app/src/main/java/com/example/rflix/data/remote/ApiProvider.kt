package com.example.rflix.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiProvider {
    private const val BASE_URL: String = "https://api.themoviedb.org/3/"
    private val okHttpClient: OkHttpClient

    init {
        val httpLogging = provideLoggingInterceptor()
        okHttpClient = provideOkHttpClient(httpLogging)
    }

    fun provideApi(url: String? = null) = Retrofit.Builder()
            .baseUrl(url ?: BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    private fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient().newBuilder()
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .run {
                addInterceptor(interceptor)
            }.build()

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        //level = if(BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        level = HttpLoggingInterceptor.Level.NONE
    }
}