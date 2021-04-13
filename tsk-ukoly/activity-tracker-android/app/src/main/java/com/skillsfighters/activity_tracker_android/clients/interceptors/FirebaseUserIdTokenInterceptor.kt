package com.skillsfighters.activity_tracker_android.clients.interceptors

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class FirebaseUserIdTokenInterceptor(private val debugToken: String? = null) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (debugToken != null) {
            val modifiedRequest = request.newBuilder()
                .addHeader(FIREBASE_TOKEN_HEADER, debugToken)
                .build()
            return chain.proceed(modifiedRequest)
        }

        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Log.e("User error", "User is not logged in.")
                throw Exception("User is not logged in.")
            }
            else {
                val task = user.getIdToken(true)
                val tokenResult = Tasks.await(task)
                val idToken = tokenResult.token

                if (idToken == null) {
                    Log.e("Token error", "idToken is null")
                    throw Exception("idToken is null")
                }
                else {
                    val modifiedRequest = request.newBuilder()
                        .addHeader(FIREBASE_TOKEN_HEADER, idToken)
                        .build()
                    return chain.proceed(modifiedRequest)
                }
            }
        } catch (e: Exception) {
            Log.e("User error", e.toString())
            throw IOException(e.message)
        }

    }

    companion object {
        private const val FIREBASE_TOKEN_HEADER = "firebaseToken"
    }
}