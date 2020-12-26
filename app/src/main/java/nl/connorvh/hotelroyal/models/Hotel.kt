package nl.connorvh.hotelroyal.models

data class Hotel (
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val mobile: Long = 0,
    val website: String = "",
    val image: String = "",
    val desc: String = "",
    val fcmToken: String = ""
)