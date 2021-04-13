package com.skillsfighters.activity_tracker_android.entities

data class GroupCreated(val name: String,
                        val parentId: Long? = null) {
    constructor(group: Group) : this(group.name, group.parentId)
}