package com.ezcorp.ezwallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class History : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.history

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                R.id.credit_card -> {
                    val intent = Intent(this, Wallet::class.java)
                    startActivity(intent)
                }

                R.id.transfers -> {
                    val intent = Intent(this, Transfers::class.java)
                    startActivity(intent)
                }

                R.id.profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                }
            }
            false
        }
    }
}