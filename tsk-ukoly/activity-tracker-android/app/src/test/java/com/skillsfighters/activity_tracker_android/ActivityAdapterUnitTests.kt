package com.skillsfighters.activity_tracker_android

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.skillsfighters.activity_tracker_android.adapters.ActivityAdapter
import com.skillsfighters.activity_tracker_android.clients.ActivityApiClient
import com.skillsfighters.activity_tracker_android.entities.Activity
import com.skillsfighters.activity_tracker_android.entities.ActivityCreated
import com.skillsfighters.activity_tracker_android.entities.ActivityUpdated
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ActivityAdapterUnitTests {

    lateinit var activityAdapter: ActivityAdapter

    lateinit var context: Context

    @Mock
    lateinit var activityApiClient: ActivityApiClient

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        MockitoAnnotations.initMocks(this)

        activityAdapter = ActivityAdapter(context, activityApiClient)
        activityAdapter.currentGroupId = 0
    }


    @Test
    fun refreshActivities() {
        val activityList: List<Activity> = listOf(Activity(1, 0, 0, 0, 0))
        activityAdapter.currentGroupId = 1

        `when`(activityApiClient.getActivitiesByParentId(1))
            .thenReturn(Observable.just(activityList))

        activityAdapter.refreshActivities()
        await()
            .until(activityAdapter.activities::isNotEmpty)

        assert(activityAdapter.activities == activityList)
        assert(activityAdapter.itemCount == activityList.size)
    }

    @Test
    fun addActivity() {
        val activityCreated = ActivityCreated(0)

        `when`(activityApiClient.addActivity(activityCreated))
            .thenReturn(Completable.never())

        activityAdapter.addActivity(activityCreated)

        verify(activityApiClient)
            .addActivity(activityCreated)
        verifyNoMoreInteractions(activityApiClient)
    }

    @Test
    fun editActivity() {
        val activityUpdated = ActivityUpdated(0, 0)

        `when`(activityApiClient.updateActivity(activityUpdated))
            .thenReturn(Completable.never())

        activityAdapter.editActivity(activityUpdated)

        verify(activityApiClient)
            .updateActivity(activityUpdated)
        verifyNoMoreInteractions(activityApiClient)
    }

    @Test
    fun deleteActivity() {
        val activity = Activity(1, 0, 0, 0, 0)

        `when`(activityApiClient.deleteActivity(activity.id))
            .thenReturn(Completable.never())

        activityAdapter.deleteActivity(activity)

        verify(activityApiClient)
            .deleteActivity(activity.id)
        verifyNoMoreInteractions(activityApiClient)
    }

    @Test
    fun activitiesToIntervalsDay() {
        activityAdapter.activities = arrayListOf(
            Activity(1, 0, 0, 0, 0),
            Activity(2, 0, 0, 0, 0),
            Activity(3, 0, 0, 0, 0),
            Activity(4, 0, 0, 10000000000, 0),
            Activity(5, 0, 0, 10000000000, 0)
        )

        activityAdapter.timeInterval = ActivityAdapter.TimeInterval.DAY

        activityAdapter.activitiesToIntervals()

        assert(activityAdapter.intervalsToShow.size == 2)
        assert(activityAdapter.intervalsToShow.toList()[0].second == 3)
        assert(activityAdapter.intervalsToShow.toList()[1].second == 2)
    }

    @Test
    fun activitiesToIntervalsWeek() {
        activityAdapter.activities = arrayListOf(
            Activity(1, 0, 0, 0, 0),
            Activity(2, 0, 0, 0, 0),
            Activity(3, 0, 0, 0, 0),
            Activity(4, 0, 0, 10000000000, 0),
            Activity(5, 0, 0, 10000000000, 0)
        )

        activityAdapter.timeInterval = ActivityAdapter.TimeInterval.WEEK

        activityAdapter.activitiesToIntervals()

        assert(activityAdapter.intervalsToShow.size == 2)
        assert(activityAdapter.intervalsToShow.toList()[0].second == 3)
        assert(activityAdapter.intervalsToShow.toList()[1].second == 2)
    }

    @Test
    fun activitiesToIntervalsMonth() {
        activityAdapter.activities = arrayListOf(
            Activity(1, 0, 0, 0, 0),
            Activity(2, 0, 0, 0, 0),
            Activity(3, 0, 0, 0, 0),
            Activity(4, 0, 0, 10000000000, 0),
            Activity(5, 0, 0, 10000000000, 0)
        )

        activityAdapter.timeInterval = ActivityAdapter.TimeInterval.MONTH

        activityAdapter.activitiesToIntervals()

        assert(activityAdapter.intervalsToShow.size == 2)
        assert(activityAdapter.intervalsToShow.toList()[0].second == 3)
        assert(activityAdapter.intervalsToShow.toList()[1].second == 2)
    }

    @Test
    fun activitiesToIntervalsYear() {
        activityAdapter.activities = arrayListOf(
            Activity(1, 0, 0, 0, 0),
            Activity(2, 0, 0, 0, 0),
            Activity(3, 0, 0, 0, 0),
            Activity(4, 0, 0, 1000000000000, 0),
            Activity(5, 0, 0, 1000000000000, 0)
        )

        activityAdapter.timeInterval = ActivityAdapter.TimeInterval.YEAR

        activityAdapter.activitiesToIntervals()

        assert(activityAdapter.intervalsToShow.size == 2)
        assert(activityAdapter.intervalsToShow.toList()[0].second == 3)
        assert(activityAdapter.intervalsToShow.toList()[1].second == 2)
    }
}
