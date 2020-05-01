package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.StatusId
import java.lang.Exception

object ApiHelper {

    suspend fun <T : Any> handleRequest(statusId: StatusId, request: suspend () -> T): Status<T> {
        return try {
            Status.Success(statusId, request.invoke())
        } catch (e: Exception) {
            Status.Error(statusId, e.message)
        }
    }
}