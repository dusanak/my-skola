package com.skillsfighters.activity_tracker_android

import com.google.gson.Gson
import com.skillsfighters.activity_tracker_android.clients.GroupApiClient
import com.skillsfighters.activity_tracker_android.entities.Group
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class GroupApiClientUnitTests {

    private var mockWebServer = MockWebServer()

    private lateinit var groupApiClient: GroupApiClient

    @Before
    fun setup() {
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(FirebaseUserIdTokenInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .build()

        groupApiClient = retrofit.create(GroupApiClient::class.java)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getCountTest() {
        val count = 10L

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(count))

        mockWebServer.enqueue(response)

        var resultCount: Long? = null

        groupApiClient.getCount(10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                resultCount = result
            }

        val recordedRequest = mockWebServer.takeRequest()

        assert("GET" == recordedRequest.method)
        assert("/group/count?groupid=10" == recordedRequest.path)
        assert(resultCount == count)
    }

    @Test
    fun getGroupTest() {
        val group = Group(0, 0, 0, "", 0)

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(group))

        mockWebServer.enqueue(response)

        var resultGroup: Group? = null

        groupApiClient.getGroup(10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                resultGroup = group
            }

        val recordedRequest = mockWebServer.takeRequest()

        assert("GET" == recordedRequest.method)
        assert("/group/show?groupid=10" == recordedRequest.path)
        assert(resultGroup == group)
    }

}