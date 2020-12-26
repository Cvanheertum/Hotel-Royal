package nl.connorvh.hotelroyal.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.nav_header_main.view.*
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.databinding.ActivityAccountBinding
import nl.connorvh.hotelroyal.firebase.FirestoreClass
import nl.connorvh.hotelroyal.models.User

class AccountActivity : BaseActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FirestoreClass().loadUserData(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAccountActivity)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
            actionBar.title = resources.getString(R.string.account)
        }

        binding.toolbarAccountActivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    fun setUserDataInUI(user: User) {
        Glide
            .with(this@AccountActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivProfileUserImage)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L) {
            binding.etMobile.setText(user.mobile.toString())
        }

    }
}