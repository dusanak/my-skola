package com.skillsfighters.activity_tracker_android.clients

import com.skillsfighters.activity_tracker_android.clients.interceptors.FirebaseUserIdTokenInterceptor
import com.skillsfighters.activity_tracker_android.entities.UIOptions
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UIOptionsApiClient {

    @GET("uioptions/show")
    fun getUIOptions(@Query("groupid") groupId: Long): Observable<UIOptions>

    @GET("uioptions/showall")
    fun getAllUIOptions(): Observable<List<UIOptions>>

    @GET("uioptions/showallbyparentid")
    fun getAllUIOptionsByParentId(@Query("parentid") groupId: Long): Observable<List<UIOptions>>

    @POST("uioptions/add")
    fun addUIOptions(@Body uiOptions: UIOptions): Completable

    @DELETE("uioptions/delete")
    fun deleteUIOptions(@Query("id") id: Long) : Completable

    @PUT("uioptions/update")
    fun updateUIOptions(@Body activity: UIOptions) : Completable

    companion object {

        fun create(): UIOptionsApiClient {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(FirebaseUserIdTokenInterceptor())
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.0.2.2:8080/") //For launching from a virtual android device
                .client(okHttpClient)
                .build()

            return retrofit.create(UIOptionsApiClient::class.java)
        }
    }
}