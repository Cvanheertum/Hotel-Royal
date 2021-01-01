package nl.connorvh.hotelroyal.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nl.connorvh.hotelroyal.activities.*
import nl.connorvh.hotelroyal.models.Hotel
import nl.connorvh.hotelroyal.models.User
import nl.connorvh.hotelroyal.utils.Constants

class FirestoreClass {

    private val db = Firebase.firestore

    fun registerUser(activity: SignUpActivity, userinfo: User) {

        db.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userinfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.userRegisteredSuccess()
            }.addOnFailureListener {

                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun updateUserAccountData(activity: AccountActivity, userHashMap: HashMap<String, Any>) {
        db.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile data successfully updated!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating user.")
                Toast.makeText(activity, "Profile updating failed!", Toast.LENGTH_LONG).show()
            }
    }

    fun loadUserData(activity: Activity, readHotelsList: Boolean = false) {

        db.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)

                if (loggedInUser != null) {
                    when (activity) {
                        is SignInActivity -> {
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser, readHotelsList)
                        }
                        is AccountActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun getCurrentUserId(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""

        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun createHotel(activity: CreateHotelActivity, hotel: Hotel) {
        db.collection(Constants.HOTELS)
            .document()
            .set(hotel, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Hotel created successfully.")

                Toast.makeText(activity, "Hotel created successfully.", Toast.LENGTH_SHORT).show()
                activity.hotelCreatedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a hotel.",
                    e
                )
            }
    }

    fun updateHotelDetails(activity: HotelDetailsActivity, hotel: Hotel) {
        db.collection(Constants.HOTELS)
            .document(hotel.documentId)
            .set(hotel, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Hotel changed successfully.")

                activity.updateUI(hotel)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while changing a hotel.",
                    e
                )
            }
    }

    fun getFollowedHotelsList(activity: MainActivity) {
        db.collection(Constants.HOTELS)
            .whereArrayContains(Constants.FOLLOWERS, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())

                val hotelsList: ArrayList<Hotel> = ArrayList()

                for (i in document.documents) {
                    val hotel = i.toObject(Hotel::class.java)!!
                    hotel.documentId = i.id
                    hotelsList.add(hotel)
                }

                activity.populateHotelsListToUI(hotelsList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a hotel!")
            }
    }

    fun getAllHotelsList(activity: SearchActivity) {
        db.collection(Constants.HOTELS)
            .orderBy(Constants.NAME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                Log.i("TAG", document.documents.toString())

                val hotelsList: ArrayList<Hotel> = ArrayList()

                for (i in document.documents) {
                    val hotel = i.toObject(Hotel::class.java)!!
                    hotel.documentId = i.id
                    hotelsList.add(hotel)
                }

                activity.populateHotelsListToUI(hotelsList)
            }

    }

    fun getHotelDetails(activity: HotelDetailsActivity, documentId: String) {
        db.collection(Constants.HOTELS)
            .document(documentId)
            .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Warning", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("Success", "Current data: ${snapshot.data}")
                snapshot.id

                activity.setupHotelDetails(snapshot.toObject(Hotel::class.java)!!, snapshot.id)
            } else {
                Log.d("No data", "Current data: null")
            }
        }
    }
}