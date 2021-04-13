package com.skillsfighters.activity_tracker_android.entities

data class ActivityUpdated(val id: Long,
                           val timestamp: Long) {
    constructor(activity: Activity) : this(activity.id, activity.timestamp)
}