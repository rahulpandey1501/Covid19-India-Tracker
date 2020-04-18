package com.rpandey.covid19tracker_india.data

sealed class Status<T> {
    class Fetching<T> : Status<T>()
    class Success<T>(val data: T) : Status<T>()
    class Error<T>(val errorMessage: String? = null) : Status<T>()
}