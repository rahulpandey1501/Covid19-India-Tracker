package com.rpandey.covid19tracker_india.data

sealed class Status<T>(val requestId: RequestId) {
    class Fetching<T>(requestId: RequestId) : Status<T>(requestId)
    class Success<T>(requestId: RequestId, val data: T) : Status<T>(requestId)
    class Error<T>(requestId: RequestId, val errorMessage: String? = null) : Status<T>(requestId)
}