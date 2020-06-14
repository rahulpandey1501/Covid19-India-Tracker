package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.RequestId
import com.rpandey.covid19tracker_india.data.processor.ResponseProcessor
import java.lang.Exception

class ApiHelper {

    suspend fun <T : Any> handleRequest(requestId: RequestId, request: suspend () -> T): Status<T> {
        return try {
            Status.Success(requestId, request.invoke())
        } catch (e: Exception) {
            Status.Error(requestId, e.message)
        }
    }

    suspend fun <T : Any> syncAndProcess(requestId: RequestId, request: suspend () -> T, callback: suspend (Status<T>) -> Unit, processor: ResponseProcessor<T>) {
        val requestStatus = handleRequest(requestId, request)

        if (requestStatus is Status.Success) {
            val data = requestStatus.data
            processor.process(data)
        }

        callback(requestStatus)
    }
}