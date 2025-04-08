package com.shubham.womensafety

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.firebase.ui.auth.AuthUI
import com.shubham.womensafety.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up Data Binding
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Initialize DrawerLayout
        drawerLayout = binding.drawerLayout

        // Set up NavController
        val navController = this.findNavController(R.id.myNavHostFragment)

        // ✅ Set top-level destinations (like Dashboard) so drawer toggle appears
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.dashBoardFragment),
            drawerLayout
        )

        // ✅ Attach toolbar with nav controller and drawer
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

        // Handle Navigation Drawer item clicks
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
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Optional drawer listener if you want to react to open/close events
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
