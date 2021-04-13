package com.skillsfighters.activity_tracker_android.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.firebase.iid.FirebaseInstanceId
import com.skillsfighters.activity_tracker_android.R
import com.skillsfighters.activity_tracker_android.adapters.ActivityAdapter
import com.skillsfighters.activity_tracker_android.adapters.GroupAdapter
import com.skillsfighters.activity_tracker_android.clients.ActivityApiClient
import com.skillsfighters.activity_tracker_android.clients.GroupApiClient
import com.skillsfighters.activity_tracker_android.clients.TokenApiClient
import com.skillsfighters.activity_tracker_android.clients.UIOptionsApiClient
import com.skillsfighters.activity_tracker_android.entities.ActivityCreated
import com.skillsfighters.activity_tracker_android.entities.Group
import com.skillsfighters.activity_tracker_android.entities.GroupCreated
import com.skillsfighters.activity_tracker_android.entities.UIOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_group.*


class GroupActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var selectedGroup: Group? = null
    private var selectedGroupUIOptions: UIOptions? = null

    private val groupApiClient = GroupApiClient.create()
    private val activityApiClient = ActivityApiClient.create()
    private val uiOptionsApiClient = UIOptionsApiClient.create()

    val groupAdapter = GroupAdapter(this, groupApiClient, uiOptionsApiClient) { onGroupSelected(it) }
    val activityAdapter = ActivityAdapter(this, activityApiClient)

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.extras!!.get("Data")) {
                "Refresh" -> {
                    if (rv_item_list.adapter == groupAdapter) {
                        groupAdapter.refreshGroups()
                    }
                    else if (rv_item_list.adapter == activityAdapter) {
                        activityAdapter.refreshActivities()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        fcmTokenReg()

        //Sets the adapter for the spinner element
        ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            ActivityAdapter.TimeInterval.values().map {
                this.getString(it.resourceId)
            }
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this

        rv_item_list.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter("MyData")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.group_buttons, menu)
        return true
    }

    //Overrides the default actionbar back button behavior instead of returning to parent activity
    //returns to the activity from which the current activity was started
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //If the user is in root group, goes back to auth activity, else goes to parent group.
    override fun onBackPressed() {
        if (selectedGroup == null) {
            super.onBackPressed()
        }
        else {
            returnToParentGroup()
        }
    }

    //Sets the actions for group_buttons on the actionbar
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.refresh -> {
            if (rv_item_list.adapter == groupAdapter)
                groupAdapter.refreshGroups()
            else if (rv_item_list.adapter == activityAdapter)
                activityAdapter.refreshActivities()
            true
        }
        R.id.graphs -> {
            showGraph()
            true
        }
        R.id.calendar -> {
            showCalendar()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    //If nothing is selected in spinner
    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }

    //Handling of spinner timeInterval selection
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            activityAdapter.timeInterval = ActivityAdapter.TimeInterval.values()[position]
        }
        activityAdapter.refreshActivities()
    }

    private fun onGroupSelected(group: Group) {
        selectedGroup = group
        getSelectedUIOptions()
    }

    //Updates UI. Called when group is changed.
    private fun updateUI() {
        groupAdapter.currentGroupId = selectedGroup?.id ?: 0L
        activityAdapter.currentGroupId = selectedGroup?.id ?: 0L

        rv_item_list.adapter = groupAdapter

        groupAdapter.refreshGroups()
        activityAdapter.refreshActivities()

        spinner.visibility = View.GONE

        group_name.text = selectedGroup?.name ?: getString(R.string.root_group)

        checkSelectedGroupContainsActivities()

        fab_add.setOnClickListener { addActivityOrGroup() }

        val background = group_name.background as GradientDrawable
        background.setStroke(4, selectedGroupUIOptions?.color?:resources.getColor(R.color.colorPrimary))

    }

    //Sets the selectedGroup to the parentGroup of current selectedGroup
    private fun returnToParentGroup() {
        spinner.visibility = View.GONE

        if (selectedGroup!!.parentId == 0L) {
            selectedGroup = null
            selectedGroupUIOptions = null
            updateUI()
        }
        else {
            groupApiClient.getGroup(selectedGroup!!.parentId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    selectedGroup = result
                    getSelectedUIOptions()
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

    //Creates a dialog for entering selectedGroup name and afterwards adds the selectedGroup
    private fun addGroup(): Boolean {
        var isSuccessful = false
        val dialogBuilder = AlertDialog.Builder(this)

        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp

        dialogBuilder.setView(input)
        dialogBuilder.setTitle(getString(R.string.new_group))
        dialogBuilder.setMessage(getString(R.string.add_group_message))
        dialogBuilder.setPositiveButton(getString(R.string.save)) { dialog, whichButton ->
            groupAdapter.addGroup(
                GroupCreated(
                    input.text.toString(),
                    selectedGroup?.id
                )
            )
            isSuccessful = true
        }
        dialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, whichButton ->
            isSuccessful = false
        }
        val b = dialogBuilder.create()
        b.show()
        return isSuccessful
    }

    private fun addActivity() {
        activityAdapter.addActivity(ActivityCreated(selectedGroup!!.id))
    }

    //In case the selectedGroup does not contain neither activities nor groups
    //the user must decide which one will be added
    private fun addActivityOrGroup() {
        if (groupAdapter.itemCount > 0 || selectedGroup == null) {
            addGroup()
        } else if (activityAdapter.itemCount > 0) {
            addActivity()
        } else {
            val dialogBuilder = AlertDialog.Builder(this)

            dialogBuilder.setTitle(getString(R.string.activity_or_group))

            dialogBuilder.setPositiveButton(getString(R.string.group)) { dialog, whichButton ->
                if (addGroup()) {
                    rv_item_list.adapter = groupAdapter
                }
                updateUI()
            }
            dialogBuilder.setNeutralButton(getString(R.string.activity)) { dialog, whichButton ->
                rv_item_list.adapter = activityAdapter
                addActivity()
                updateUI()
            }
            val b = dialogBuilder.create()
            b.show()
        }
    }

    private fun showGraph() {
        val intent = Intent(this, GraphActivity::class.java)
        val extras = Bundle()

        extras.putParcelable("GROUP", selectedGroup)
        intent.putExtras(extras)

        startActivity(intent)
    }

    private fun showCalendar() {
        val intent = Intent(this, CalendarActivity::class.java)
        val extras = Bundle()

        extras.putParcelable("GROUP", selectedGroup)
        intent.putExtras(extras)

        startActivity(intent)
    }

   //Checks if the selected group contains any activities
   //if not, it is considered to contain groups
    private fun checkSelectedGroupContainsActivities() {
        if (selectedGroup == null) {
            rv_item_list.adapter = groupAdapter
            spinner.visibility = View.GONE
            return
        }

        groupApiClient.containsActivites(selectedGroup ?.id ?: 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) {
                    rv_item_list.adapter = activityAdapter
                    spinner.visibility = View.VISIBLE
                } else {
                    rv_item_list.adapter = groupAdapter
                    spinner.visibility = View.GONE
                }
            }
    }

    private fun getSelectedUIOptions() {
        uiOptionsApiClient.getUIOptions(selectedGroup?.id?:0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                selectedGroupUIOptions = it
                updateUI()
            }
    }

    //Updates color of group name and notifies backend
    private fun updateColor(color: Int) {
        if (selectedGroupUIOptions == null) {
            selectedGroupUIOptions = UIOptions(
                0,
                selectedGroup?.id?:0,
                color,
                null,
                null)
            uiOptionsApiClient.addUIOptions(selectedGroupUIOptions as UIOptions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { getSelectedUIOptions() }

        } else {
            selectedGroupUIOptions = UIOptions(
                selectedGroupUIOptions!!.id,
                selectedGroupUIOptions!!.groupId,
                color,
                selectedGroupUIOptions!!.unit,
                selectedGroupUIOptions!!.icon)
            uiOptionsApiClient.updateUIOptions(selectedGroupUIOptions as UIOptions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { getSelectedUIOptions() }
        }
    }

    //Color picker
    fun chooseColor(view: View) {
        if (selectedGroup != null) {
            ColorPickerDialogBuilder
                .with(this)
                .setTitle(getString(R.string.color_picker_title))
                .initialColor(0xffffffff.toInt())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener { selectedColor ->
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, selectedColor, allColors ->
                    updateColor(selectedColor)
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .build()
                .show()
        }
    }

    // Sends the current FCMToken/registrationToken to backend
    private fun fcmTokenReg() {
        val tokenApiClient = TokenApiClient.create()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    tokenApiClient.newToken(it.result!!.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                }
                else {
                    Log.e("FCM Token Error", it.exception.toString())
                }
            }
    }
}
