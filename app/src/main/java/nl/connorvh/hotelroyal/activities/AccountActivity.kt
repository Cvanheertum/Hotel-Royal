package nl.connorvh.hotelroyal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nl.connorvh.hotelroyal.R
import nl.connorvh.hotelroyal.databinding.ActivityAccountBinding

class AccountActivity : BaseActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}