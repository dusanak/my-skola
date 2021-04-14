package com.skillsfighters.activity_tracker_android.clients

import com.skillsfighters.activity_tracker_android.clients.interceptors.FirebaseUserIdTokenInterceptor
import com.skillsfighters.activity_tracker_android.entities.GroupCreated
import com.skillsfighters.activity_tracker_android.entities.Group
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface GroupApiClient {

    @GET("group/show")
    fun getGroup(@Query("groupid") groupId: Long): Observable<Group>

    @GET("group/showall")
    fun getGroups(): Observable<List<Group>>

    @GET("group/showallbyparentid")
    fun getGroupsByParentId(@Query("parentid") parentId: Long): Observable<List<Group>>

    @GET("group/count")
    fun getCount(@Query("groupid") groupId: Long): Observable<Long>

    @GET("group/count")
    fun getCount(@Query("groupid") groupId: Long,
                 @Query("startdate") startDate: Long,
                 @Query("enddate") endDate: Long): Observable<Long>

    @GET("group/containsactivities")
    fun containsActivites(@Query("parentid") parentId: Long) : Observable<Boolean>

    @PUT("group/add")
    fun addGroup(@Body group: GroupCreated): Completable

    @DELETE("group/delete")
    fun deleteGroup(@Query("groupid") id: Long) : Completable

    @POST("group/update")
    fun updateGroup(@Body group: Group) : Completable

    @POST("group/defaults")
    fun createDefaultGroups(@Body locale: String) : Observable<List<Group>>

    companion object {

        fun create(): GroupApiClient {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(FirebaseUserIdTokenInterceptor("skillsfighters"))
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.0.2.2:8080/") //For launching from a virtual android device
                .client(okHttpClient)
                .build()

            return retrofit.create(GroupApiClient::class.java)
        }
    }
}