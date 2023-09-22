package com.ezcorp.ezwallet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnMyWallet = findViewById<Button>(R.id.my_wallet_btn)
        btnMyWallet.setOnClickListener {
            val intent = Intent(this, Wallet::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.home

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.credit_card -> {
                    val intent = Intent(this, Wallet::class.java)
                    startActivity(intent)
                }

                R.id.transfers -> {
                    val intent = Intent(this, Transfers::class.java)
                    startActivity(intent)
                }

                R.id.history -> {
                    val intent = Intent(this, History::class.java)
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