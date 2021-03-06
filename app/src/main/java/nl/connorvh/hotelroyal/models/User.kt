package nl.connorvh.hotelroyal.models

data class User (
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String= "",
    val mobile: Long = 0,
    val admin: Boolean = false,
    val hotels: ArrayList<String> = ArrayList()
)