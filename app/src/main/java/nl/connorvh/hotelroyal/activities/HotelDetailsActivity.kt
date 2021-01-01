package nl.connorvh.hotelroyal.activities

import android.app.Activity
import android.os.Bundle
import com.bumptech.glide.Glide
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.databinding.ActivityHotelDetailsBinding
import nl.connorvh.hotelroyal.firebase.FirestoreClass
import nl.connorvh.hotelroyal.models.Hotel
import nl.connorvh.hotelroyal.utils.Constants

class HotelDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityHotelDetailsBinding
    private lateinit var hotel: Hotel

    private var hotelChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var hotelDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            hotelDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getHotelDetails(this, hotelDocumentId)

        binding.ivHotelFollow.setOnClickListener {
            hotelChanged = true

            showProgressDialog(resources.getString(R.string.please_wait))

            if (hotel.followers.contains(getCurrentUserID())) {
                hotel.followers.remove(getCurrentUserID())
            } else {
                hotel.followers.add(getCurrentUserID())
            }

            FirestoreClass().updateHotelDetails(this, hotel)
        }
    }

    private fun setupActionBar(title: String) {
        setSupportActionBar(binding.toolbarHotelDetailsActivity)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
            actionBar.title = title
        }

        binding.toolbarHotelDetailsActivity.setNavigationOnClickListener {
            if (hotelChanged) {
                setResult(Activity.RESULT_OK)
            }

            onBackPressed()
        }
    }

    fun setupHotelDetails(hotel: Hotel, id: String) {
        hotel.documentId = id
        this.hotel = hotel

        setupActionBar(hotel.name)

        Glide
            .with(this)
            .load(hotel.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivHotelDetailsImage)

        binding.tvHotelName.text = hotel.name
        binding.tvHotelDesc.text = hotel.desc
        binding.tvHotelWebsite.text = hotel.website
        binding.tvHotelEmail.text = hotel.email
        binding.tvHotelMobile.text = "+31 ${hotel.mobile}"

        updateUI(hotel)
    }

    fun updateUI(hotel: Hotel) {

        binding.tvFollowersCount.text = hotel.followers.count().toString()

        // Check if current user follows this hotel
        if (hotel.followers.contains(getCurrentUserID())) {
            binding.ivHotelFollow.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            binding.ivHotelFollow.setImageResource(R.drawable.ic_favorite_outlined)
        }

        hideProgressDialog()
    }
}