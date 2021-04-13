package com.skillsfighters.activity_tracker_android.entities

data class ActivityCreated(val groupId: Long) {
    constructor(activity: Activity) : this(activity.groupId)
}