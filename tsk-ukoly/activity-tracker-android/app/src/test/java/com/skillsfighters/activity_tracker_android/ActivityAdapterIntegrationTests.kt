package com.skillsfighters.activity_tracker_android

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.skillsfighters.activity_tracker_android.adapters.ActivityAdapter
import com.skillsfighters.activity_tracker_android.clients.ActivityApiClient
import com.skillsfighters.activity_tracker_android.entities.Activity
import com.skillsfighters.activity_tracker_android.entities.ActivityCreated
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ActivityAdapterIntegrationTests {

    private var mockWebServer = MockWebServer()

    private lateinit var context: Context

    private lateinit var activityApiClient: ActivityApiClient

    private lateinit var activityAdapter: ActivityAdapter

    @Before
    fun setup() {
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder()
            .build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .build()

        context = ApplicationProvider.getApplicationContext()

        activityApiClient = retrofit.create(ActivityApiClient::class.java)

        activityAdapter = ActivityAdapter(context, activityApiClient)

        activityAdapter.currentGroupId = 0L

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
    fun refreshActivitiesTest() {
        val activityList = mutableListOf<Activity>()
        activityList.add(Activity(0, 0, 0, 0, 0))
        activityList.add(Activity(1, 0, 0, 0, 0))
        activityList.add(Activity(2, 0, 0, 0, 0))

        activityAdapter.currentGroupId = 1

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(Gson().toJson(activityList))

        mockWebServer.enqueue(response)

        activityAdapter.refreshActivities()

        val recordedRequest = mockWebServer.takeRequest()

        assert("GET" == recordedRequest.method)
        assert("/activity/showallbyparentid?groupid=1" == recordedRequest.path)
        assert(activityAdapter.activities == activityList)
    }

    @Test
    fun addActivityTest() {
        val activity = Activity(0, 0, 0, 0, 0)

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)

        mockWebServer.enqueue(response)

        activityAdapter.addActivity(ActivityCreated(activity))

        val recordedRequest = mockWebServer.takeRequest()

        assert("PUT" == recordedRequest.method)
        assert("/activity/add" == recordedRequest.path)
        assert(Gson().toJson(ActivityCreated(activity)) == recordedRequest.body.readUtf8())
    }

}