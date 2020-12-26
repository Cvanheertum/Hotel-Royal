package nl.connorvh.hotelroyal.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.databinding.ActivitySignInBinding
import nl.connorvh.hotelroyal.firebase.FirestoreClass
import nl.connorvh.hotelroyal.models.User

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    override fun onResume() {
        super.onResume()
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser() {

        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Succes", "signInWithEmail:success")

                        FirestoreClass().loadUserData(this)

                        Toast.makeText(
                            this,
                            "You have successfully signed in.",
                            Toast.LENGTH_LONG)
                            .show()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Error", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }

    fun signInSuccess(user: User) {

        hideProgressDialog()

        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        this.finish()
    }
}