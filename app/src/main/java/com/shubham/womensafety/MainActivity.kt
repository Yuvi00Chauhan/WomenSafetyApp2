package com.shubham.womensafety

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.firebase.ui.auth.AuthUI
import com.shubham.womensafety.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up Data Binding
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Initialize DrawerLayout
        drawerLayout = binding.drawerLayout

        // Set up NavController
        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        // Set up Navigation Item Selection Listener
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout_user -> {
                    AuthUI.getInstance().signOut(this)
                }
                R.id.guardianInfo -> {
                    navController.navigate(R.id.action_dashBoardFragment_to_guardianInfo)
                }
                R.id.mapsActivity -> {
                    navController.navigate(R.id.action_dashBoardFragment_to_mapsActivity)
                }
            }
            // Close the drawer after selection
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Set up DrawerListener to manage visibility
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Optional: Handle drawer slide if needed
            }

            override fun onDrawerOpened(drawerView: View) {
                // Hide any UI elements if needed when the drawer is opened
                // For example, you can hide the main content or change its visibility
            }

            override fun onDrawerClosed(drawerView: View) {
                // Show any UI elements if needed when the drawer is closed
            }

            override fun onDrawerStateChanged(newState: Int) {
                // Optional: Handle drawer state changes if needed
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }
}