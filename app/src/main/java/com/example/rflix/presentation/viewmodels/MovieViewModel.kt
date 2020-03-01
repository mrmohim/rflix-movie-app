package com.example.rflix.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rflix.data.repository.MovieRepository
import com.example.rflix.data.response.CreditsResponse
import com.example.rflix.data.response.MovieResponse
import com.example.rflix.data.response.VideoResponse
import com.example.rflix.data.response.ViewData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieViewModel: ViewModel() {
    var movieStatus: MutableLiveData<ViewData<MovieResponse>> = MutableLiveData()
        private set

    var videoStatus: MutableLiveData<ViewData<VideoResponse>> = MutableLiveData()
        private set

    var creditsStatus: MutableLiveData<ViewData<CreditsResponse>> = MutableLiveData()
        private set

    private var movieRepository = MovieRepository()
    private var disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun cancelRequest() {
        disposable.clear()
    }

    fun getTopRatedMovie(apiKey: String, language: String, page: String) {
        val response = ViewData<MovieResponse>()
        disposable.add(movieRepository.getTopRatedMovie(apiKey, language, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                response.status = ViewData.Status.RUNNING
                response.operationName = ViewData.Operation.TOP_RATED
                movieStatus.value = response
            }
            .subscribe({
                if (it.isSuccessful) {
                    response.status = ViewData.Status.SUCCESS
                    response.message = it.message()
                    response.value.add(it.body()!!)
                } else {
                    response.status = ViewData.Status.ERROR
                    response.message = it.message()
                }
                movieStatus.value = response
            }) {
                response.status = ViewData.Status.ERROR
                response.message = it.message.toString()
                movieStatus.value = response
            })
    }

    fun getUpcomingMovie(apiKey: String, language: String, page: String) {
        val response = ViewData<MovieResponse>()
        disposable.add(movieRepository.getUpcomingMovie(apiKey, language, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                response.status = ViewData.Status.RUNNING
                response.operationName = ViewData.Operation.UPCOMING
                movieStatus.value = response
            }
            .subscribe({
                if (it.isSuccessful) {
                    response.status = ViewData.Status.SUCCESS
                    response.message = it.message()
                    response.value.add(it.body()!!)
                } else {
                    response.status = ViewData.Status.ERROR
                    response.message = it.message()
                }
                movieStatus.value = response
            }) {
                response.status = ViewData.Status.ERROR
                response.message = it.message.toString()
                movieStatus.value = response
            })
    }

    fun getPopularMovie(apiKey: String, language: String, page: String) {
        val response = ViewData<MovieResponse>()
        disposable.add(movieRepository.getPopularMovie(apiKey, language, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                response.status = ViewData.Status.RUNNING
                response.operationName = ViewData.Operation.POPULAR
                movieStatus.value = response
            }
            .subscribe({
                if (it.isSuccessful) {
                    response.status = ViewData.Status.SUCCESS
                    response.message = it.message()
                    response.value.add(it.body()!!)
                } else {
                    response.status = ViewData.Status.ERROR
                    response.message = it.message()
                }
                movieStatus.value = response
            }) {
                response.status = ViewData.Status.ERROR
                response.message = it.message.toString()
                movieStatus.value = response
            })
    }

    fun getNowPlayingMovie(apiKey: String, language: String, page: String) {
        val response = ViewData<MovieResponse>()
        disposable.add(movieRepository.getNowPlayingMovie(apiKey, language, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                response.status = ViewData.Status.RUNNING
                response.operationName = ViewData.Operation.NOW_PLAYING
                movieStatus.value = response
            }
            .subscribe({
                if (it.isSuccessful) {
                    response.status = ViewData.Status.SUCCESS
                    response.message = it.message()
                    response.value.add(it.body()!!)
                } else {
                    response.status = ViewData.Status.ERROR
                    response.message = it.message()
                }
                movieStatus.value = response
            }) {
                response.status = ViewData.Status.ERROR
                response.message = it.message.toString()
                movieStatus.value = response
            })
    }

    fun getSimilarMovie(movie_id: Int, apiKey: String, language: String, page: String) {
        val response = ViewData<MovieResponse>()
        disposable.add(movieRepository.getSimilarMovie(movie_id, apiKey, language, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                response.status = ViewData.Status.RUNNING
                response.operationName = ViewData.Operation.SIMILAR
                movieStatus.value = response
            }
            .subscribe({
                if (it.isSuccessful) {
                    response.status = ViewData.Status.SUCCESS
                    response.message = it.message()
                    response.value.add(it.body()!!)
                } else {
                    response.status = ViewData.Status.ERROR
                    response.message = it.message()
                }
                movieStatus.value = response
            }) {
                response.status = ViewData.Status.ERROR
                response.message = it.message.toString()
                movieStatus.value = response
            })
    }

    fun getSearch(apiKey: String, language: String, query: String, page: String, includeAdult: String) {
        val response = ViewData<MovieResponse>()
        disposable.add(movieRepository.getSearch(apiKey, language, query, page, includeAdult)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                response.status = ViewData.Status.RUNNING
                response.operationName = ViewData.Operation.SEARCH
                movieStatus.value = response
            }
            .subscribe({
                if (it.isSuccessful) {
                    response.status = ViewData.Status.SUCCESS
                    response.message = it.message()
                    response.value.add(it.body()!!)
                } else {
                    response.status = ViewData.Status.ERROR
                    response.message = it.message()
                }
                movieStatus.value = response
            }) {
                response.status = ViewData.Status.ERROR
                response.message = it.message.toString()
                movieStatus.value = response
            })
    }

    fun getVideos(movie_id: Int, apiKey: String, language: String) {
        val response = ViewData<VideoResponse>()
        disposable.add(movieRepository.getVideos(movie_id, apiKey, language)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                response.status = ViewData.Status.RUNNING
                response.operationName = ViewData.Operation.VIDEO
                videoStatus.value = response
            }
            .subscribe({
                if (it.isSuccessful) {
                    response.status = ViewData.Status.SUCCESS
                    response.message = it.message()
                    response.value.add(it.body()!!)
                } else {
                    response.status = ViewData.Status.ERROR
                    response.message = it.message()
                }
                videoStatus.value = response
            }) {
                response.status = ViewData.Status.ERROR
                response.message = it.message.toString()
                videoStatus.value = response
            })
    }

    fun getCredits(movie_id: Int, apiKey: String) {
        val response = ViewData<CreditsResponse>()
        disposable.add(movieRepository.getCredits(movie_id, apiKey)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                response.status = ViewData.Status.RUNNING
                response.operationName = ViewData.Operation.CREDITS
                creditsStatus.value = response
            }
            .subscribe({
                if (it.isSuccessful) {
                    response.status = ViewData.Status.SUCCESS
                    response.message = it.message()
                    response.value.add(it.body()!!)
                } else {
                    response.status = ViewData.Status.ERROR
                    response.message = it.message()
                }
                creditsStatus.value = response
            }) {
                response.status = ViewData.Status.ERROR
                response.message = it.message.toString()
                creditsStatus.value = response
            })
    }
}