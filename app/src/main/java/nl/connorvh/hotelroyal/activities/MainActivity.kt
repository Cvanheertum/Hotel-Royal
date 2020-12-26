package nl.connorvh.hotelroyal.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.databinding.ActivityMainBinding
import nl.connorvh.hotelroyal.firebase.FirestoreClass
import nl.connorvh.hotelroyal.models.User

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        binding.navView.setNavigationItemSelectedListener(this)
        FirestoreClass().signInUser(this)

        binding.appBarMain.mainContent.btnSignOut.setOnClickListener {
            signOut()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_drawer_home -> {
                Toast.makeText(this@MainActivity, "Home", Toast.LENGTH_LONG).show()
            }
            R.id.nav_drawer_search -> {
                Toast.makeText(this@MainActivity, "Search", Toast.LENGTH_LONG).show()
            }
            R.id.nav_drawer_account -> {
                Toast.makeText(this@MainActivity, "Account", Toast.LENGTH_LONG).show()
            }
        }

        return true
    }

    fun updateNavigationUserDetails(user: User) {
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.navView.nav_user_image);

        binding.navView.tv_username.text = user.name
    }
}