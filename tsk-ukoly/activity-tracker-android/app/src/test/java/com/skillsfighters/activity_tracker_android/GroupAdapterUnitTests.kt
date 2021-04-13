package com.skillsfighters.activity_tracker_android

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.skillsfighters.activity_tracker_android.adapters.GroupAdapter
import com.skillsfighters.activity_tracker_android.clients.GroupApiClient
import com.skillsfighters.activity_tracker_android.clients.UIOptionsApiClient
import com.skillsfighters.activity_tracker_android.entities.Group
import com.skillsfighters.activity_tracker_android.entities.GroupCreated
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
class GroupAdapterUnitTests {

    lateinit var groupAdapter: GroupAdapter

    lateinit var context: Context

    @Mock
    lateinit var groupApiClient: GroupApiClient

    @Mock
    lateinit var uiOptionsApiClient: UIOptionsApiClient

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        MockitoAnnotations.initMocks(this)

        groupAdapter = GroupAdapter(context, groupApiClient, uiOptionsApiClient) {}
        groupAdapter.currentGroupId = 0
    }


    @Test
    fun refreshGroups() {
        val groupList: List<Group> = listOf(Group(1, 0, 0, "TEST", 0, 0))

        `when`(groupApiClient.getGroupsByParentId(0))
            .thenReturn(Observable.just(groupList))

        groupAdapter.refreshGroups()
        await()
            .until(groupAdapter.groups::isNotEmpty)

        assert(groupAdapter.groups == groupList)
        assert(groupAdapter.itemCount == groupList.size)
    }

    @Test
    fun addGroup() {
        val groupCreated = GroupCreated("TEST", 0)

        `when`(groupApiClient.addGroup(groupCreated))
            .thenReturn(Completable.never())

        groupAdapter.addGroup(groupCreated)

        verify(groupApiClient)
            .addGroup(groupCreated)
        verifyNoMoreInteractions(groupApiClient)
    }

    @Test
    fun editGroup() {
        val group = Group(1, 0, 0, "TEST", 0, 0)

        `when`(groupApiClient.updateGroup(group))
            .thenReturn(Completable.never())

        groupAdapter.editGroup(group)

        verify(groupApiClient)
            .updateGroup(group)
        verifyNoMoreInteractions(groupApiClient)
    }

    @Test
    fun deleteGroup() {
        val group = Group(1, 0, 0, "TEST", 0, 0)

        `when`(groupApiClient.deleteGroup(group.id))
            .thenReturn(Completable.never())

        groupAdapter.deleteGroup(group)

        verify(groupApiClient)
            .deleteGroup(group.id)
        verifyNoMoreInteractions(groupApiClient)
    }
}
