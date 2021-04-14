package com.skillsfighters.activity_tracker_android.clients

import com.skillsfighters.activity_tracker_android.clients.interceptors.FirebaseUserIdTokenInterceptor
import com.skillsfighters.activity_tracker_android.entities.Activity
import com.skillsfighters.activity_tracker_android.entities.ActivityCreated
import com.skillsfighters.activity_tracker_android.entities.ActivityUpdated
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ActivityApiClient {

    @GET("activity/showall")
    fun getActivities(): Observable<List<Activity>>

    @GET("activity/showallbyparentid")
    fun getActivitiesByParentId(@Query("groupid") id: Long): Observable<List<Activity>>

    @GET("activity/showallbyparentid")
    fun getActivitiesByParentId(@Query("groupid") id: Long,
                                @Query("startdate") startDate: Long,
                                @Query("enddate") endDate: Long): Observable<List<Activity>>

    @PUT("activity/add")
    fun addActivity(@Body activity: ActivityCreated): Completable

    @DELETE("activity/delete")
    fun deleteActivity(@Query("activityid") id: Long) : Completable

    @POST("activity/update")
    fun updateActivity(@Body activity: ActivityUpdated) : Completable

    companion object {

        fun create(): ActivityApiClient {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(FirebaseUserIdTokenInterceptor("skillsfighters"))
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.0.2.2:8080/") //For launching from a virtual android device
                .client(okHttpClient)
                .build()

            return retrofit.create(ActivityApiClient::class.java)
        }
    }
}