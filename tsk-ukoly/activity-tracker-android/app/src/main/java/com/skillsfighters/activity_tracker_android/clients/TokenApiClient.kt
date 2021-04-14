package com.skillsfighters.activity_tracker_android.clients

import com.skillsfighters.activity_tracker_android.clients.interceptors.FirebaseUserIdTokenInterceptor
import io.reactivex.Completable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenApiClient {

    @POST("token/new")
    fun newToken(@Header("registrationToken") registrationToken: String): Completable

    companion object {

        fun create(): TokenApiClient {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(FirebaseUserIdTokenInterceptor("skillsfighters"))
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.0.2.2:8080/") //For launching from a virtual android device
                .client(okHttpClient)
                .build()

            return retrofit.create(TokenApiClient::class.java)
        }
    }
}