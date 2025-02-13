package com.example.runningapp

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.runningapp.databinding.ActivityMainBinding
import com.example.runningapp.db.RunDao
import com.example.runningapp.other.Constants
import com.example.runningapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment:NavHostFragment
    @Inject
    lateinit var runDao:RunDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemReselectedListener { /* don't do anything */ }


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.runFragment, R.id.statisticsFragment, R.id.fragmentSettings -> binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }

        // Handle the initial intent
        if (intent.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navigateToTrackingFragmentFromNotification()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isLocationEnabled()){
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle new intents (when app is already running)
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navigateToTrackingFragmentFromNotification()
        }
    }

    private fun navigateToTrackingFragmentFromNotification() {
        navHostFragment.navController.navigate(R.id.trackingFragment, null, NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(R.id.nav_graph, false)  // Use the nav graph ID as popUp destination
            .build()
        )
    }
}