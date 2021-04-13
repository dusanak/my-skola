package com.skillsfighters.activity_tracker_android.adapters

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.skillsfighters.activity_tracker_android.R
import com.skillsfighters.activity_tracker_android.clients.ActivityApiClient
import com.skillsfighters.activity_tracker_android.entities.Activity
import com.skillsfighters.activity_tracker_android.entities.ActivityCreated
import com.skillsfighters.activity_tracker_android.entities.ActivityUpdated
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap

class ActivityAdapter(
    private val context: Context,
    private val activityApiClient: ActivityApiClient) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    var currentGroupId = 0L
    var activities: ArrayList<Activity> = ArrayList()

    var intervalsToShow: LinkedHashMap<String, Int> = linkedMapOf()
    var timeInterval = TimeInterval.NONE

    enum class TimeInterval(val resourceId: Int) {
        NONE(R.string.none),
        DAY(R.string.day),
        WEEK(R.string.week),
        MONTH(R.string.month),
        YEAR(R.string.year)
    }

    class ActivityViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivityViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val intervalToShow = intervalsToShow.keys.toTypedArray()[position]
        when (timeInterval) {
            TimeInterval.NONE -> {
                holder.view.text_value.text = intervalToShow
                holder.view.number_of_activities.text = ""
                holder.view.item_color_indicator.visibility = View.GONE

                holder.view.edit_button.visibility = View.VISIBLE
                holder.view.delete_button.visibility = View.VISIBLE

                holder.view.edit_button.setOnClickListener {
                    if (position < activities.size) {
                        editActivityDialog(
                            holder,
                            activities[position]
                        )
                    }
                }
                holder.view.delete_button.setOnClickListener {
                    if (position < activities.size) {
                        deleteActivityDialog(
                            holder,
                            activities[position]
                        )
                    }
                }
            }
            TimeInterval.DAY, TimeInterval.WEEK, TimeInterval.MONTH, TimeInterval.YEAR -> {
                holder.view.text_value.text = intervalToShow
                holder.view.number_of_activities.text = intervalsToShow[intervalToShow].toString()

                holder.view.edit_button.visibility = View.INVISIBLE
                holder.view.delete_button.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount() = intervalsToShow.size

    fun refreshActivities() {
        if (currentGroupId == 0L) {
            return
        }

        activities.clear()
        activityApiClient.getActivitiesByParentId(currentGroupId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                for (i in result) {
                    activities.add(i)
                }
                activitiesToIntervals()
                notifyDataSetChanged()
            }, { error ->
                Toast.makeText(
                    context,
                    context.getString(R.string.refresh_error) + ": ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ERRORS", error.message.toString())
            })
    }

    //Creates from the list of activities a map of strings and their counts.
    //The strings represent a time interval as specified by the field timeInterval.
    fun activitiesToIntervals() {
        intervalsToShow.clear()

        when (timeInterval) {
            TimeInterval.NONE -> {
                val format = SimpleDateFormat.getDateTimeInstance()
                activities.associateTo(intervalsToShow) { Pair(format.format(it.timestamp), 1) }
            }
            TimeInterval.DAY -> {
                val format = SimpleDateFormat.getDateInstance()
                activities.forEach {
                    val date = format.format(it.timestamp)
                    if (intervalsToShow.contains(date)) {
                        intervalsToShow[date] = intervalsToShow[date]!!.plus(1)
                    }
                    else {
                        intervalsToShow[date] = 1
                    }
                }
            }
            TimeInterval.WEEK -> {
                val format = SimpleDateFormat(
                    "'" + context.getString(R.string.week) + "'" +
                             " ww',' yyyy",
                    Locale.getDefault()
                )
                activities.forEach {
                    val date = format.format(it.timestamp)
                    if (intervalsToShow.contains(date)) {
                        intervalsToShow[date] = intervalsToShow[date]!!.plus(1)
                    }
                    else {
                        intervalsToShow[date] = 1
                    }
                }
            }
            TimeInterval.MONTH -> {
                val format = SimpleDateFormat("MM/yyyy", Locale.getDefault())
                
                activities.forEach {
                    val date = format.format(it.timestamp)
                    if (intervalsToShow.contains(date)) {
                        intervalsToShow[date] = intervalsToShow[date]!!.plus(1)
                    }
                    else {
                        intervalsToShow[date] = 1
                    }
                }
            }
            TimeInterval.YEAR -> {
                val format = SimpleDateFormat("yyyy", Locale.getDefault())

                activities.forEach {
                    val date = format.format(it.timestamp)
                    if (intervalsToShow.contains(date)) {
                        intervalsToShow[date] = intervalsToShow[date]!!.plus(1)
                    }
                    else {
                        intervalsToShow[date] = 1
                    }
                }
            }
        }
    }

    fun addActivity(activity: ActivityCreated) {
        activityApiClient.addActivity(activity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refreshActivities() }, { throwable ->
                Toast.makeText(
                    context,
                    context.getString(R.string.add_error) + ": ${throwable.message}",
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    fun editActivity(activity: ActivityUpdated) {
        activityApiClient.updateActivity(activity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refreshActivities() }, { throwable ->
                Toast.makeText(
                    context,
                    context.getString(R.string.edit_error) + ": ${throwable.message}",
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    private fun editActivityDialog(holder: ActivityViewHolder, activity: Activity) {
        val calendar = GregorianCalendar()
        calendar.time = Date(activity.timestamp)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hourOfDay: Int, minute: Int ->
                        calendar.set(year, month, day, hourOfDay, minute)
                        editActivity(ActivityUpdated(activity.id, calendar.time.time))
                    }
                val timePickerDialog = TimePickerDialog(
                    context,
                    timeSetListener,
                    calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            }

        val datePickerDialog = DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun deleteActivity(activity: Activity) {
        activityApiClient.deleteActivity(activity.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refreshActivities() }, { throwable ->
                Toast.makeText(
                    context,
                    context.getString(R.string.delete_error) + ": ${throwable.message}",
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    private fun deleteActivityDialog(holder: ActivityViewHolder, activity: Activity) {
        deleteActivity(activity)
    }
}