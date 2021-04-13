package com.skillsfighters.activity_tracker_android.activities

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.skillsfighters.activity_tracker_android.R
import com.skillsfighters.activity_tracker_android.clients.ActivityApiClient
import com.skillsfighters.activity_tracker_android.clients.GroupApiClient
import com.skillsfighters.activity_tracker_android.clients.UIOptionsApiClient
import com.skillsfighters.activity_tracker_android.entities.Activity
import com.skillsfighters.activity_tracker_android.entities.Group
import com.skillsfighters.activity_tracker_android.entities.UIOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_graph.*
import java.text.SimpleDateFormat
import java.util.*

class GraphActivity : AppCompatActivity() {

    private var selectedGroup: Group? = null
    private var selectedGroupUIOptions: UIOptions? = null

    private val childGroups: MutableList<Group> = mutableListOf()
    private val childGroupCounts: MutableMap<Group, Long> = mutableMapOf()
    private val childGroupUIOptions: MutableMap<Long, UIOptions> = mutableMapOf()

    private val childActivities: MutableList<Activity> = mutableListOf()
    private val numberOfActivitiesByDays: SortedMap<String, Int> = sortedMapOf()

    private val groupApiClient: GroupApiClient = GroupApiClient.create()
    private val activityApiClient: ActivityApiClient = ActivityApiClient.create()
    private val uiOptionsApiClient = UIOptionsApiClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        selectedGroup = intent.getParcelableExtra("GROUP")

        group_name.text = selectedGroup?.name ?: getString(R.string.root_group)

        pie_chart.visibility = View.VISIBLE
        bar_chart.visibility = View.GONE

        if (selectedGroup != null) {
            updateGroupColor()
        }

        checkSelectedGroupContainsActivities()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.graph_buttons, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.pie_chart_button -> {
            bar_chart.visibility = View.GONE
            pie_chart.visibility = View.VISIBLE
            true
        }
        R.id.bar_chart_button -> {
            pie_chart.visibility = View.GONE
            bar_chart.visibility = View.VISIBLE
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    private fun checkSelectedGroupContainsActivities() {
        groupApiClient.containsActivites(selectedGroup ?.id ?: 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) {
                    getActivities()
                } else {
                    getGroups()
                }
            }
    }

    private fun getActivities() {
        activityApiClient.getActivitiesByParentId(selectedGroup!!.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                childActivities.addAll(result)

                if (result.isNotEmpty()) {
                    activitiesToDays()
                    generateActivityBarGraph()
                    generateActivityPieGraph()
                }
            }, { error ->
                Toast.makeText(
                    this,
                    getString(R.string.refresh_error) + ": ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ERRORS", error.message.toString())
            })
    }

    private fun getGroups() {
        groupApiClient.getGroupsByParentId(selectedGroup?.id ?: 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                childGroups.addAll(result)
                if (childGroups.isNotEmpty()) {
                    getGroupCounts()
                }
            }, { error ->
                Toast.makeText(
                    this,
                    getString(R.string.refresh_error) + ": ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ERRORS", error.message.toString())
            })
    }

    private fun getGroupCounts() {
        childGroups.forEach {
            groupApiClient.getCount(it.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    childGroupCounts[it] = result
                    getChildGroupUIOptions()
                }, { error ->
                    Toast.makeText(
                        this,
                        getString(R.string.refresh_error) + ": ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("ERRORS", error.message.toString())
                })
        }
    }

    private fun getChildGroupUIOptions() {
        uiOptionsApiClient.getAllUIOptionsByParentId(selectedGroup?.id ?: 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ result ->
                childGroupUIOptions.clear()
                result.forEach { uiOptions ->
                    childGroupUIOptions[uiOptions.groupId] = uiOptions
                }
                generateGroupBarGraph()
                generateGroupPieGraph()
            }
    }

    private fun activitiesToDays() {
        val format = SimpleDateFormat.getDateInstance()
        childActivities.forEach {
            val date = format.format(it.timestamp)
            if (numberOfActivitiesByDays.contains(date)) {
                numberOfActivitiesByDays[date] = numberOfActivitiesByDays[date]!!.plus(1)
            }
            else {
                numberOfActivitiesByDays[date] = 1
            }
        }
    }

    private fun generateGroupBarGraph() {
        val barDataSets: MutableList<BarDataSet> = mutableListOf()

        for (i in 0 until childGroups.size) {
            barDataSets.add(BarDataSet(
                listOf(BarEntry(i.toFloat(), childGroupCounts[childGroups[i]]?.toFloat() ?: 0f)),
                childGroups[i].name))

            barDataSets[i].color = childGroupUIOptions[childGroups[i].id]?.color ?: ColorTemplate.MATERIAL_COLORS[i % 4]
        }

        val barData = BarData(barDataSets.toList())
        barData.setValueTextSize(16f)

        bar_chart.xAxis.valueFormatter = IndexAxisValueFormatter(childGroups.map { "" })
        bar_chart.legend.textSize = 16f
        bar_chart.description.text = ""
        bar_chart.data = barData
        bar_chart.setFitBars(true)
        bar_chart.invalidate()
    }

    private fun generateGroupPieGraph() {
        val pieEntries: MutableList<PieEntry> = mutableListOf()
        val colors: MutableList<Int> = mutableListOf()

        for (i in 0 until childGroups.size) {
            if (childGroupCounts[childGroups[i]] == 0L) {
                continue
            }

            val pieEntry = PieEntry(childGroupCounts[childGroups[i]]?.toFloat() ?: 0f, childGroups[i].name)
            pieEntries.add(pieEntry)
            colors.add(childGroupUIOptions[childGroups[i].id]?.color ?: ColorTemplate.MATERIAL_COLORS[i % 4])

            pieEntry.data
        }
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setValueTextColors(colors.map { color -> getContrastTextColor(color) })
        pieData.setValueTextSize(16f)

        pie_chart.setDrawEntryLabels(false)
        pie_chart.legend.textSize = 16f
        pie_chart.description.text = ""
        pie_chart.data = pieData
        pie_chart.invalidate()
    }

    private fun generateActivityBarGraph() {
        val barDataSets: MutableList<BarDataSet> = mutableListOf()

        for (i in 0 until numberOfActivitiesByDays.size) {
            val activityDay = numberOfActivitiesByDays.keys.toTypedArray()[i]
            barDataSets.add(BarDataSet(
                listOf(BarEntry(i.toFloat(), numberOfActivitiesByDays[activityDay]?.toFloat() ?: 0f)),
                activityDay))
            barDataSets[i].color = ColorTemplate.MATERIAL_COLORS[i % 4]
        }

        val barData = BarData(barDataSets.toList())
        barData.setValueTextSize(16f)

        bar_chart.xAxis.valueFormatter = IndexAxisValueFormatter(childGroups.map { "" })
        bar_chart.legend.textSize = 16f
        bar_chart.description.text = ""
        bar_chart.data = barData
        bar_chart.setFitBars(true)
        bar_chart.invalidate()
    }

    private fun generateActivityPieGraph() {
        val pieEntries: MutableList<PieEntry> = mutableListOf()

        numberOfActivitiesByDays.forEach {
            pieEntries.add(PieEntry(it.value.toFloat(), it.key))
        }

        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()

        val pieData = PieData(pieDataSet)
        pieData.setValueTextColors(pieDataSet.colors.map { color -> getContrastTextColor(color) })
        pieData.setValueTextSize(16f)

        pie_chart.setDrawEntryLabels(false)
        pie_chart.legend.textSize = 16f
        pie_chart.description.text = ""
        pie_chart.data = pieData
        pie_chart.invalidate()
    }

    private fun getContrastTextColor(color: Int) : Int {
        val luminance = ( 0.299 * Color.red(color) +
                0.587 * Color.green(color) +
                0.114 * Color.blue(color)) / 255

        return if (luminance > 0.5) {
            Color.BLACK
        } else {
            Color.WHITE
        }
    }
}
