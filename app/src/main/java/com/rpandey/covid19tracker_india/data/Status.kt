package com.rpandey.covid19tracker_india.data

sealed class Status<T>(val statusId: StatusId) {
    class Fetching<T>(statusId: StatusId) : Status<T>(statusId)
    class Success<T>(statusId: StatusId, val data: T) : Status<T>(statusId)
    class Error<T>(statusId: StatusId, val errorMessage: String? = null) : Status<T>(statusId)
}