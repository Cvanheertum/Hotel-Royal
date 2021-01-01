package nl.connorvh.hotelroyal.activities

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header_main.view.*
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.adapters.HotelItemsAdapter
import nl.connorvh.hotelroyal.databinding.ActivityMainBinding
import nl.connorvh.hotelroyal.firebase.FirestoreClass
import nl.connorvh.hotelroyal.models.Hotel
import nl.connorvh.hotelroyal.models.User
import nl.connorvh.hotelroyal.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val ACCOUNT_REQUEST_CODE: Int = 11
        const val CREATE_HOTEL_REQUEST_CODE: Int = 2
        const val HOTEL_DETAILS_REQUEST_CODE: Int = 4
        const val HOTEL_SEARCH_REQUEST_CODE: Int = 13
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var mUserName: String
    private var mUserIsAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        binding.navView.setNavigationItemSelectedListener(this)
        FirestoreClass().loadUserData(this, true)

        binding.appBarMain.fabCreateHotel.setOnClickListener {
            if (mUserIsAdmin) {
                val intent = Intent(this, CreateHotelActivity::class.java)
                startActivityForResult(intent, CREATE_HOTEL_REQUEST_CODE)
            } else {
                Toast.makeText(this, "Sorry you are not allowed", Toast.LENGTH_LONG).show()
            }

        }

    }

    fun populateHotelsListToUI(hotelsList: ArrayList<Hotel>) {
        hideProgressDialog()

        if (hotelsList.size > 0) {
            binding.appBarMain.mainContent.rvHotelsList.visibility = View.VISIBLE
            binding.appBarMain.mainContent.tvNoHotelsAvailable.visibility = View.GONE

            binding.appBarMain.mainContent.rvHotelsList.layoutManager = LinearLayoutManager(this)
            binding.appBarMain.mainContent.rvHotelsList.setHasFixedSize(true)

            val adapter = HotelItemsAdapter(this, hotelsList)
            binding.appBarMain.mainContent.rvHotelsList.adapter = adapter

            adapter.setOnClickListener(object : HotelItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Hotel) {

                    val intent = Intent(this@MainActivity, HotelDetailsActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)

                    startActivityForResult(intent, HOTEL_DETAILS_REQUEST_CODE)
                }
            })

        } else {
            binding.appBarMain.mainContent.rvHotelsList.visibility = View.GONE
            binding.appBarMain.mainContent.tvNoHotelsAvailable.visibility = View.VISIBLE
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        binding.appBarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu_white)

        binding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ACCOUNT_REQUEST_CODE) {

            FirestoreClass().loadUserData(this)

        } else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_HOTEL_REQUEST_CODE) {

            FirestoreClass().getFollowedHotelsList(this)

        } else if (resultCode == Activity.RESULT_OK && requestCode == HOTEL_DETAILS_REQUEST_CODE) {

            FirestoreClass().getFollowedHotelsList(this)

        } else if (resultCode == Activity.RESULT_OK && requestCode == HOTEL_SEARCH_REQUEST_CODE) {

            FirestoreClass().getFollowedHotelsList(this)

        } else {

            Log.e("onActivityResult", "Canceled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_drawer_sign_out -> {
                auth.signOut()
                startActivity(Intent(this@MainActivity, IntroActivity::class.java))
                finish()
            }
            R.id.nav_drawer_search -> {
                startActivityForResult(
                    Intent(this@MainActivity, SearchActivity::class.java),
                    HOTEL_SEARCH_REQUEST_CODE
                )
            }
            R.id.nav_drawer_account -> {
                startActivityForResult(
                    Intent(this, AccountActivity::class.java),
                    ACCOUNT_REQUEST_CODE
                )
            }
        }

        return true
    }

    fun updateNavigationUserDetails(user: User, readHotelsList: Boolean) {

        mUserName = user.name
        mUserIsAdmin = user.admin

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.navView.nav_user_image)

        binding.navView.tv_username.text = user.name

        // hide fab if no admin
        if (mUserIsAdmin) {
            binding.appBarMain.fabCreateHotel.visibility = View.VISIBLE
        } else {
            binding.appBarMain.fabCreateHotel.visibility = View.GONE
        }

        if (readHotelsList) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getFollowedHotelsList(this)
        }
    }
}