package com.app.bustracking.presentation.views.activities

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.app.bustracking.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint


private val TAG = HomeActivity::class.simpleName.toString()

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(com.app.bustracking.R.id.fl_container_02) as NavHostFragment?
        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            setupWithNavController(binding.bottomNav, navController)


            //
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                // Update the selected item in the BottomNavigationView based on the destination
                updateSelectedItem(destination.id)
//                binding.bottomNav.selectedItemId = destination.id
            }


            // Set up the BottomNavigationView item selection listener
            binding.bottomNav.setOnNavigationItemSelectedListener { item ->
                // Navigate to the corresponding destination when an item is selected
                onNavDestinationSelected(item, navController)
                true
            }


        }


    }

    private fun updateSelectedItem(destinationId: Int) {
        // Find the menu item that corresponds to the destination ID
        for (i in 0 until binding.bottomNav.menu.size()) {
            val menuItem = binding.bottomNav.menu.getItem(i)
            if (menuItem.itemId == destinationId) {
                // Set the item as selected
                menuItem.isChecked = true

//                binding.bottomNav.selectedItemId = destinationId
            }
        }
    }
}