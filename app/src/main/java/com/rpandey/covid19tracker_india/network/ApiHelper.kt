package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.data.Status
import java.lang.Exception

object ApiHelper {

    suspend fun <T : Any> handleRequest(request: suspend () -> T): Status<T> {
        return try {
            Status.Success(request.invoke())
        } catch (e: Exception) {
            Status.Error()
        }
    }
}