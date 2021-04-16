package com.skillsfighters.activity_tracker_android.adapters

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.skillsfighters.activity_tracker_android.R
import com.skillsfighters.activity_tracker_android.clients.GroupApiClient
import com.skillsfighters.activity_tracker_android.clients.UIOptionsApiClient
import com.skillsfighters.activity_tracker_android.entities.Group
import com.skillsfighters.activity_tracker_android.entities.GroupCreated
import com.skillsfighters.activity_tracker_android.entities.UIOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*


class GroupAdapter(
    private val context: Context,
    private val groupApiClient: GroupApiClient,
    private val uiOptionsApiClient: UIOptionsApiClient,
    val onGroupClick: (Group) -> Unit) :
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    var currentGroupId = 0L
    var groups: MutableList<Group> = mutableListOf()
    var uiOptionsList: MutableMap<Long, UIOptions> = mutableMapOf()

    class GroupViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.view.text_value.text = groups[position].name
        holder.view.number_of_activities.text = "0"
        holder.view.setOnClickListener { onGroupClick(groups[position]) }
        holder.view.edit_button.setOnClickListener {
            if (position < groups.size) {
                editGroupDialog(holder, groups[position])
            }
        }
        holder.view.delete_button.setOnClickListener {
            if (position < groups.size) {
                deleteGroupDialog(holder, groups[position])
            }
        }

        groupApiClient.getCount(groups[position].id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                holder.view.number_of_activities.text = result.toString()
            }, { error ->
                Log.e("ERRORS", error.message.toString())
            })

        holder.view.item_color_indicator.background = ColorDrawable(
            uiOptionsList[groups[position].id]?.color ?: 0xffffffff.toInt())
    }

    override fun getItemCount() = groups.size

    fun refreshGroups() {
        groupApiClient.getGroupsByParentId(currentGroupId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                groups.clear()
                groups.addAll(result)
                notifyDataSetChanged()
                Toast.makeText(
                    context,
                    context.getString(R.string.refreshed),
                    Toast.LENGTH_SHORT
                ).show()

                // commented out for debug purposes
                /*if (currentGroupId == 0L && groups.isEmpty()) {
                    generateDefaultGroups()
                }*/

                refreshUIOptions()
            }, { error ->
                Toast.makeText(
                    context,
                    context.getString(R.string.refresh_error) + ": ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ERRORS", error.message.toString())
            })
    }

    fun refreshUIOptions() {
        uiOptionsApiClient.getAllUIOptionsByParentId(currentGroupId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                uiOptionsList.clear()
                result.forEach { uiOptions ->
                    uiOptionsList[uiOptions.groupId] = uiOptions
                }
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

    fun addGroup(group: GroupCreated) {
        groupApiClient.addGroup(group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refreshGroups() }, { throwable ->
                Toast.makeText(
                    context,
                    context.getString(R.string.add_error) + ": ${throwable.message}",
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    fun editGroup(group: Group) {
        groupApiClient.updateGroup(group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refreshGroups() }, { throwable ->
                Toast.makeText(
                    context,
                    context.getString(R.string.edit_error) + ": ${throwable.message}",
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    private fun editGroupDialog(holder: GroupViewHolder, group: Group) {
        val dialogBuilder = AlertDialog.Builder(context)

        val input = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp

        dialogBuilder.setView(input)
        dialogBuilder.setTitle(context.getString(R.string.edit_group))
        dialogBuilder.setMessage(context.getString(R.string.edit_group_message))
        dialogBuilder.setPositiveButton(context.getString(R.string.save)) { dialog, whichButton ->
            editGroup(Group(group, input.text.toString()))
        }
        dialogBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, whichButton ->
        }
        val b = dialogBuilder.create()
        b.show()
    }

    fun deleteGroup(group: Group) {
        groupApiClient.deleteGroup(group.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refreshGroups() }, { throwable ->
                Toast.makeText(
                    context,
                    context.getString(R.string.delete_error) + ": ${throwable.message}",
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    private fun deleteGroupDialog(holder: GroupViewHolder, group: Group) {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle(context.getString(R.string.delete_group))
        dialogBuilder.setMessage(context.getString(R.string.delete_group_message) + " ${group.name}?")
        dialogBuilder.setPositiveButton(context.getString(R.string.delete)) { dialog, whichButton ->
            deleteGroup(group)
        }
        dialogBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, whichButton ->
        }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun generateDefaultGroups() {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle(context.getString(R.string.defaults_title))
        dialogBuilder.setMessage(context.getString(R.string.default_message))
        dialogBuilder.setPositiveButton(context.getString(R.string.yes)) { dialog, whichButton ->
            groupApiClient.createDefaultGroups(Locale.getDefault().toLanguageTag())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ refreshGroups() }, { throwable ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.default_groups_error) + ": ${throwable.message}",
                        Toast.LENGTH_LONG
                    ).show()
                })
        }
        dialogBuilder.setNegativeButton(context.getString(R.string.no)) { dialog, whichButton ->
        }
        val b = dialogBuilder.create()
        b.show()
    }
}