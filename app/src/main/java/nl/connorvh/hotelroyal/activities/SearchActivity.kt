package nl.connorvh.hotelroyal.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.adapters.HotelItemsAdapter
import nl.connorvh.hotelroyal.databinding.ActivitySearchBinding
import nl.connorvh.hotelroyal.firebase.FirestoreClass
import nl.connorvh.hotelroyal.models.Hotel
import nl.connorvh.hotelroyal.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var hotelsList: ArrayList<Hotel> = arrayListOf<Hotel>()
    private var searchList: ArrayList<Hotel> = arrayListOf<Hotel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllHotelsList(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarHotelSearchActivity)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
            actionBar.title = resources.getString(R.string.search)
        }

        binding.toolbarHotelSearchActivity.setNavigationOnClickListener {

            setResult(Activity.RESULT_OK)

            onBackPressed()
        }
    }

    fun populateHotelsListToUI(hotelsList: ArrayList<Hotel>) {
        hideProgressDialog()
//        searchList.addAll(hotelsList)


        if (hotelsList.size > 0) {
            searchList = hotelsList
            searchList.sortByDescending { it.followers.count() }

            binding.rvHotelsList.visibility = View.VISIBLE

            binding.rvHotelsList.layoutManager = LinearLayoutManager(this)
            binding.rvHotelsList.setHasFixedSize(true)

            val adapter = HotelItemsAdapter(this, searchList)
            binding.rvHotelsList.adapter = adapter

            searchHotels()

            adapter.setOnClickListener(object : HotelItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Hotel) {

                    val intent = Intent(this@SearchActivity, HotelDetailsActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)

                    startActivity(intent)
                }
            })
        } else {
            binding.rvHotelsList.visibility = View.GONE
        }
    }

    private fun searchHotels() {
        val searchView: SearchView = binding.svHotels
        hotelsList.addAll(searchList)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {

                    searchList.clear()

                    val search = newText.toLowerCase(Locale.ROOT)

                    Log.d("SearchHotelsList", hotelsList.size.toString())
                    Log.d("SearchList", search)

                    hotelsList.forEach { hotel ->
                        Log.d("SearchList", search)
                        Log.d("SearchList", hotel.name.toLowerCase(Locale.ROOT))
                        if (hotel.name.toLowerCase(Locale.ROOT).contains(search)) {
                            searchList.add(hotel)
                        }
                    }

                    Log.d("SearchList", searchList.size.toString())

                    binding.rvHotelsList.adapter!!.notifyDataSetChanged()

                } else {
                    searchList.clear()
                    searchList.addAll(hotelsList)
                    binding.rvHotelsList.adapter!!.notifyDataSetChanged()
                }

                return true
            }

        })
    }


}
