package com.skillsfighters.activity_tracker_android.entities

import android.os.Parcel
import android.os.Parcelable

data class Group(val id: Long,
                 val createdAt: Long,
                 val updatedAt: Long,
                 val name: String,
                 val userId: Long,
                 val parentId: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    constructor(group: Group,
                name: String) : this(
        group.id,
        group.createdAt,
        group.updatedAt,
        name,
        group.userId,
        group.parentId
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
        parcel.writeString(name)
        parcel.writeLong(userId)
        parcel.writeValue(parentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }
}