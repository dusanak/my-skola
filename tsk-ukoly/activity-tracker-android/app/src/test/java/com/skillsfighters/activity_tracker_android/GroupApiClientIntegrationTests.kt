package com.skillsfighters.activity_tracker_android

import com.skillsfighters.activity_tracker_android.clients.GroupApiClient
import com.skillsfighters.activity_tracker_android.clients.interceptors.FirebaseUserIdTokenInterceptor
import com.skillsfighters.activity_tracker_android.entities.Group
import com.skillsfighters.activity_tracker_android.entities.GroupCreated
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

class GroupApiClientIntegrationTests {

    private lateinit var groupApiClient: GroupApiClient

    private val testGroupName = LocalDateTime.now().hashCode().toString()

    @Before
    fun setup() {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(FirebaseUserIdTokenInterceptor("skillsfighters"))
            .build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://localhost:8080/")
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
        val resultGroups = mutableListOf<Group>()
        groupApiClient.getGroups()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                resultGroups.addAll(result)
            }

        resultGroups.forEach {
            if (it.name == testGroupName) {
                groupApiClient.deleteGroup(it.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            }
        }
    }

    @Test
    fun addGroupTest() {
        val group = GroupCreated(testGroupName)

        var passed = false

        groupApiClient.addGroup(group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {passed = true}, {
                throwable -> passed = false
            })

        assert(passed)
    }

    @Test
    fun getGroupsByParentIdTest() {
        val groups = mutableListOf(
            GroupCreated(testGroupName),
            GroupCreated(testGroupName),
            GroupCreated(testGroupName)
        )

        groups.forEach {group ->
            groupApiClient.addGroup(group)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

        val resultGroups = mutableListOf<Group>()

        groupApiClient.getGroupsByParentId(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                resultGroups.addAll(result)
            }

        assert(resultGroups.filter { it.name == testGroupName }.size == groups.size)
    }

    @Test
    fun deleteGroups() {
        val groups = mutableListOf(
            GroupCreated(testGroupName),
            GroupCreated(testGroupName),
            GroupCreated(testGroupName)
        )

        groups.forEach { group ->
            groupApiClient.addGroup(group)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

        val resultGroups = mutableListOf<Group>()
        groupApiClient.getGroups()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                resultGroups.addAll(result)
            }

        assert(resultGroups.filter { it.name == testGroupName }.size == groups.size)

        resultGroups.filter { it.name == testGroupName }.forEach {
            groupApiClient.deleteGroup(it.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

        groupApiClient.getGroups()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                resultGroups.clear()
                resultGroups.addAll(result)
            }

        assert(resultGroups.none { it.name == testGroupName })
    }
}