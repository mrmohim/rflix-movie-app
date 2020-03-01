package com.example.rflix.data.response

class ViewData<T> {
    var error: Throwable? = null
    var status = Status.NONE
    var operationName = Operation.NONE
    var message = ""

    var value: ArrayList<T> = ArrayList()

    enum class Status {
        NONE,
        RUNNING,
        SUCCESS,
        ERROR
    }

    enum class Operation {
        NONE,
        GET,
        GET_ALL,
        DELETE_ALL,
        INSERT,
        UPDATE,
        DELETE,
        SIMILAR,
        NOW_PLAYING,
        POPULAR,
        TOP_RATED,
        UPCOMING,
        SEARCH,
        VIDEO,
        CREDITS
    }
}