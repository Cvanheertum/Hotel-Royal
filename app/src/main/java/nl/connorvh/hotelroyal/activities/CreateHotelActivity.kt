package nl.connorvh.hotelroyal.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.databinding.ActivityCreateHotelBinding
import nl.connorvh.hotelroyal.firebase.FirestoreClass
import nl.connorvh.hotelroyal.models.Hotel
import nl.connorvh.hotelroyal.utils.Constants
import java.io.IOException

class CreateHotelActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateHotelBinding

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for a user profile image URL
    private var mHotelImageURL: String = ""

    private var imageChosen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateHotelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.ivHotelImage.setOnClickListener {

            imageChosen = true

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this@CreateHotelActivity)
            } else {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnCreate.setOnClickListener {
            initializeCreatingHotel()
        }
    }

    private fun uploadHotelImage() {

        if (mSelectedImageFileUri != null) {
            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "HOTEL_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(
                    this@CreateHotelActivity,
                    mSelectedImageFileUri
                )
            )

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i(
                        "Downloadable Image URL",
                        uri.toString()
                    )

                    // assign the image url to the variable.
                    mHotelImageURL = uri.toString()

                    createHotel()

                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        this@CreateHotelActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
            }
        }
    }

    private fun createHotel() {

        val followersArrayList: ArrayList<String> = ArrayList()
        followersArrayList.add(getCurrentUserID())

        val hotel = Hotel(
            binding.etName.text.toString(),
            binding.etEmail.text.toString(),
            binding.etMobile.text.toString().toLong(),
            binding.etWebsite.text.toString(),
            mHotelImageURL,
            binding.etDescription.text.toString(),
            followersArrayList
        )

        FirestoreClass().createHotel(this@CreateHotelActivity, hotel)
    }

    private fun initializeCreatingHotel() {
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val mobile: String = binding.etMobile.text.toString().trim { it <= ' ' }
        val website: String = binding.etWebsite.text.toString().trim { it <= ' ' }
        val desc: String = binding.etDescription.text.toString().trim { it <= ' ' }

        if (imageChosen) {
            if (validateForm(name, email, mobile, website, desc)) {
                showProgressDialog(resources.getString(R.string.please_wait))
                uploadHotelImage()
            }
        } else {
            Toast.makeText(this, "Please choose an image first!", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateForm(
        name: String,
        email: String,
        mobile: String,
        website: String,
        desc: String
    ): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(mobile) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            TextUtils.isEmpty(website) -> {
                showErrorSnackBar("Please enter a website.")
                false
            }
            TextUtils.isEmpty(desc) -> {
                showErrorSnackBar("Please enter a description.")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Email is invalid."
                showErrorSnackBar("Email is invalid.")
                false
            }
            else -> {
                true
            }
        }
    }

    fun hotelCreatedSuccess() {

        hideProgressDialog()

        // Finish the screen
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@CreateHotelActivity)
            }
        } else {
            Toast.makeText(
                this,
                "Oops, you just denied the permission for storage. You can also allow it from settings.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this@CreateHotelActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(binding.ivHotelImage) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCreateHotelActivity)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
            actionBar.title = resources.getString(R.string.hotel_activity_title)
        }

        binding.toolbarCreateHotelActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}