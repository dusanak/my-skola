package com.skillsfighters.activity_tracker_android.activities

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarUtils
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.utils.CalendarProperties
import com.applandeo.materialcalendarview.utils.DateUtils
import com.skillsfighters.activity_tracker_android.R
import com.skillsfighters.activity_tracker_android.clients.ActivityApiClient
import com.skillsfighters.activity_tracker_android.clients.GroupApiClient
import com.skillsfighters.activity_tracker_android.clients.UIOptionsApiClient
import com.skillsfighters.activity_tracker_android.entities.Activity
import com.skillsfighters.activity_tracker_android.entities.Group
import com.skillsfighters.activity_tracker_android.entities.UIOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_calendar.*
import java.util.*


class CalendarActivity: AppCompatActivity() {

    private var selectedGroup: Group? = null
    private var selectedGroupUIOptions: UIOptions? = null

    private val groupSet = mutableSetOf<Group>()
    private val activityMap = mutableMapOf<Group, List<Activity>>()
    private val uiOptionsMap = mutableMapOf<Group, UIOptions>()

    private val activityApiClient = ActivityApiClient.create()
    private val groupApiClient = GroupApiClient.create()
    private val uiOptionsApiClient = UIOptionsApiClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        selectedGroup = intent.getParcelableExtra("GROUP")

        group_name.text = selectedGroup?.name ?: getString(R.string.root_group)

        if (selectedGroup != null) {
            updateGroupColor()
        }

        calendarView.showCurrentMonthPage()
        calendarView.setHeaderColor(R.color.white)
        calendarView.setHeaderLabelColor(R.color.black)

        val arrowLeft = getDrawable(R.drawable.ic_arrow_left)
        arrowLeft!!.colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
        calendarView.setPreviousButtonImage(arrowLeft)

        val arrowRight = getDrawable(R.drawable.ic_arrow_right)
        arrowRight!!.colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
        calendarView.setForwardButtonImage(arrowRight)

        refreshGroups()
    }

    private fun updateGroupColor() {
        uiOptionsApiClient.getUIOptions(selectedGroup?.id?:0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                selectedGroupUIOptions = it
                val background = group_name.background as GradientDrawable
                background.setStroke(4, selectedGroupUIOptions?.color?:resources.getColor(R.color.colorPrimary))
            }
    }


    private fun refreshGroups() {
        groupApiClient.getGroupsByParentId(selectedGroup?.id ?: 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                groupSet.clear()
                groupSet.addAll(result)

                if (result.size == 0 && selectedGroup != null) {
                    checkSelectedGroupContainsActivities()
                }
                else {
                    refreshActivities()
                }
            }
    }

    private fun refreshActivities() {
        groupSet.forEach { group ->
            activityMap.clear()
            activityApiClient.getActivitiesByParentId(group.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    activityMap[group] = result

                    if (activityMap.size == groupSet.size) {
                        refreshUIOptions()
                    }
                }
        }
    }

    private fun checkSelectedGroupContainsActivities() {
        groupApiClient.containsActivites(selectedGroup ?.id ?: 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) {
                    groupSet.add(selectedGroup!!)
                    refreshActivities()
                }
            }
    }

    private fun refreshUIOptions() {
        uiOptionsApiClient.getAllUIOptionsByParentId(selectedGroup?.id ?: 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                uiOptionsMap.clear()
                uiOptionsMap.putAll(
                    result.filter { uiOptions -> groupSet.map { group -> group.id }.contains(uiOptions.groupId) }
                        .associateBy { uiOptions -> groupSet.first { group -> group.id == uiOptions.groupId } }
                )

                updateCalendar()
            }
    }

    private fun updateCalendar() {
        val dateColorMap = mutableMapOf<Long, MutableList<Int>>()

        groupSet.forEach { group ->
            val calendar = Calendar.getInstance()
            activityMap[group]!!.forEach { activity ->
                calendar.time = Date(activity.timestamp)
                DateUtils.setMidnight(calendar)

                dateColorMap[calendar.timeInMillis] ?: run {
                    dateColorMap[calendar.timeInMillis] = mutableListOf()
                }

                dateColorMap[calendar.timeInMillis]!!.add(uiOptionsMap[group]?.color ?: Color.GRAY)
            }
        }

        val eventDays = mutableListOf<EventDay>()

        dateColorMap.forEach { (time, colors) ->
            val eventCircle = getDrawable(R.drawable.color_circle) as GradientDrawable

            if (colors.any { colors.first() != it }) {
                eventCircle.setColor(Color.GRAY)
            }
            else {
                eventCircle.setColor(colors.first())
            }

            val calendar = Calendar.getInstance()
            calendar.time = Date(time)

            eventDays.add(EventDay(calendar, eventCircle))
        }

        calendarView.setEvents(eventDays)
    }
}