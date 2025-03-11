package com.app.hydratracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.hydratracker.ui.advice.AdviceFragment
import com.app.hydratracker.ui.goals.GoalsFragment
import com.app.hydratracker.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (intent.getBooleanExtra("EXTRA_SEND_PROFILE", false)) {
            bottomNavigationView.selectedItemId = R.id.nav_account
            loadFragment(ProfileFragment())
        } else {
            loadFragment(HomeFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.nav_goals -> {
                    loadFragment(GoalsFragment())
                    true
                }

                R.id.nav_tips -> {
                    loadFragment(AdviceFragment())
                    true
                }

                R.id.nav_account -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }




}