package nl.connorvh.hotelroyal.models

import android.os.Parcel
import android.os.Parcelable

data class Review(
    val title: String = "",
    val desc: String = "",
    val date: String = "",
    val userID: String = "",
    val hotelID: String = "",
    val id: String = ""
)