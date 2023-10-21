package com.ezcorp.ezwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentMMenu = MainMenu()
        val fragmentWallet = WalletFragment()
        val fragmentTransfers = TransfersFragment()
        val fragmentHistory = HistoryFragment()
        val fragmentProfile = ProfileFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        replaceFragment(fragmentMMenu)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> { replaceFragment(fragmentMMenu) }
                R.id.credit_card -> { replaceFragment(fragmentWallet) }
                R.id.transfers -> { replaceFragment(fragmentTransfers) }
                R.id.history -> { replaceFragment(fragmentHistory) }
                R.id.profile -> { replaceFragment(fragmentProfile) }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}