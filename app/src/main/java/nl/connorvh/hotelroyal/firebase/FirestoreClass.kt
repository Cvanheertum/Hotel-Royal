package nl.connorvh.hotelroyal.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import nl.connorvh.hotelroyal.activities.AccountActivity
import nl.connorvh.hotelroyal.activities.MainActivity
import nl.connorvh.hotelroyal.activities.SignInActivity
import nl.connorvh.hotelroyal.activities.SignUpActivity
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

    fun loadUserData(activity: Activity) {

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
                            activity.updateNavigationUserDetails(loggedInUser)
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
}