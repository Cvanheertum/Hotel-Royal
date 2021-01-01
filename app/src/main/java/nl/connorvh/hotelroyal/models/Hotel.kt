package nl.connorvh.hotelroyal.models

import android.os.Parcel
import android.os.Parcelable


data class Hotel(
    val name: String = "",
    val email: String = "",
    val mobile: Long = 0,
    val website: String = "",
    val image: String = "",
    val desc: String = "",
    val followers: ArrayList<String> = ArrayList(),
    var documentId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeLong(mobile)
        parcel.writeString(website)
        parcel.writeString(image)
        parcel.writeString(desc)
        parcel.writeStringList(followers)
        parcel.writeString(documentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Hotel> {
        override fun createFromParcel(parcel: Parcel): Hotel {
            return Hotel(parcel)
        }

        override fun newArray(size: Int): Array<Hotel?> {
            return arrayOfNulls(size)
        }
    }
}