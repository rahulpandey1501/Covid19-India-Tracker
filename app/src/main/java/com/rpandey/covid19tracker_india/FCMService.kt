package com.rpandey.covid19tracker_india

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

//class FCMService: FirebaseMessagingService() {
//
//    override fun onNewToken(p0: String) {
//        super.onNewToken(p0)
//        Log.d("Covid19 FCM token: ", p0)
//    }
//
//    override fun onMessageReceived(p0: RemoteMessage) {
//        super.onMessageReceived(p0)
//        Log.d("Covid19 FCM message: ", Gson().toJson(p0))
//    }
//}