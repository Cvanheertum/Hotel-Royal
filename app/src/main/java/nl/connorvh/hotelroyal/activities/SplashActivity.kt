package nl.connorvh.hotelroyal.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.os.Handler
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.firebase.FirestoreClass

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        navigateUser()
    }

    override fun onResume() {
        super.onResume()
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    fun navigateUser() {

        val currentUserID = FirestoreClass().getCurrentUserId()

        Handler().postDelayed({

            if (currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
            }

            finish()
        },1500)
    }
}