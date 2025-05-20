package com.pablo.tfg_chatochat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PrivacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        supportActionBar?.title = "Privacidad"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
