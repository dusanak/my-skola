package com.skillsfighters.activity_tracker_android.services

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skillsfighters.activity_tracker_android.clients.TokenApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MessagingService : FirebaseMessagingService() {
    private val TAG = "FCM Service"
    private val client by lazy { TokenApiClient.create() }

    private val localBroadcastManager = LocalBroadcastManager.getInstance(this)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val intent = Intent("MyData")
        intent.putExtra("Data", remoteMessage.data["Data"])
        localBroadcastManager.sendBroadcast(intent)

        Log.d(TAG, "From: " + remoteMessage.from!!)
        Log.d(TAG, "Notification Message Body: " + (remoteMessage.notification?.body ?: "Empty"))
    }

    override fun onNewToken(registrationToken: String) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            client.newToken(registrationToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
        super.onNewToken(registrationToken)
    }
}