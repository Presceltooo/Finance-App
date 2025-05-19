package com.example.financeapp.Domain

import android.os.Parcel
import android.os.Parcelable

data class BookmarkTransaction(
    val id: String = "",
    val type: String = "", // "DEPOSIT" hoặc "WITHDRAW"
    val amount: Double = 0.0,
    val method: String = "",
    val accountNumber: String? = null,
    val accountName: String? = null,
    val scheduledTime: Long = 0L, // millis
    val status: String = "PENDING" // "PENDING", "DONE"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString() ?: "PENDING"
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeDouble(amount)
        parcel.writeString(method)
        parcel.writeString(accountNumber)
        parcel.writeString(accountName)
        parcel.writeLong(scheduledTime)
        parcel.writeString(status)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BookmarkTransaction> {
        override fun createFromParcel(parcel: Parcel): BookmarkTransaction {
            return BookmarkTransaction(parcel)
        }
        override fun newArray(size: Int): Array<BookmarkTransaction?> {
            return arrayOfNulls(size)
        }
    }
} 