package com.example.financeapp.Domain

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class TransactionDomain(
    val id: String = "",
    val amount: Double = 0.0,
    val type: String = "", // "DEPOSIT" hoặc "WITHDRAW"
    val method: String = "", // "BANK_CARD", "E_WALLET", "BANK_TRANSFER"
    val status: String = "", // "SUCCESS", "PENDING", "FAILED"
    val date: Date = Date(),
    val description: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeDouble(amount)
        parcel.writeString(type)
        parcel.writeString(method)
        parcel.writeString(status)
        parcel.writeLong(date.time)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransactionDomain> {
        override fun createFromParcel(parcel: Parcel): TransactionDomain {
            return TransactionDomain(parcel)
        }

        override fun newArray(size: Int): Array<TransactionDomain?> {
            return arrayOfNulls(size)
        }
    }
} 