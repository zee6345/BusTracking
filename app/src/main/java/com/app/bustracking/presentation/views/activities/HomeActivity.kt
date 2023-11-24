package com.app.bustracking.presentation.views.activities


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.app.bustracking.databinding.ActivityHomeBinding
import com.app.bustracking.presentation.views.fragments.home.StopsFragment
import com.app.bustracking.presentation.views.fragments.main.SelectNetworkFragment
import dagger.hilt.android.AndroidEntryPoint


private val TAG = HomeActivity::class.simpleName.toString()

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private var route: Int = 0;
    private lateinit var navController: NavController

//    private val navigationReceiver = object : BroadcastReceiver() {
//        @SuppressLint("LogNotTimber")
//        override fun onReceive(p0: Context?, p1: Intent?) {
//            p1?.apply {
//                if (action == "com.app.navigate") {
//                    val route = p1.getIntExtra("route", 0)
//                    Log.e(TAG, "onReceive: $route")
//                }
//            }
//
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = binding
        setContentView(binding.root)

        //reset
        SelectNetworkFragment.isActivityLaunched = false


        val navHostFragment =
            supportFragmentManager.findFragmentById(com.app.bustracking.R.id.fl_container_02) as NavHostFragment?
        if (navHostFragment != null) {

//            navController = findNavController(this, com.app.bustracking.R.id.nav_host_fragment)
//
//            // Set up the BottomNavigationView with the NavigationController
//            val appBarConfiguration: AppBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
//            NavigationUI.setupWithNavController(binding.bottomNav, navController)


            navController = navHostFragment.navController
            setupWithNavController(binding.bottomNav, navController)


//            binding.bottomNav.setOnNavigationItemSelectedListener { item ->

            // Apply animation
//                animateBottomNavigation(item.itemId)
//
//                navigateToFragment(item.itemId)

//                true
//            }

        }

//        val filter = IntentFilter("com.app.navigate")
//        LocalBroadcastManager.getInstance(this).registerReceiver(navigationReceiver, filter)

    }

    private fun navigateToFragment(itemId: Int) {
        when (itemId) {
            com.app.bustracking.R.id.mapsFragment -> navController.navigate(com.app.bustracking.R.id.mapsFragment)
            com.app.bustracking.R.id.routesFragment -> navController.navigate(com.app.bustracking.R.id.routesFragment)
            com.app.bustracking.R.id.stopsFragment -> navController.navigate(com.app.bustracking.R.id.stopsFragment)
            com.app.bustracking.R.id.infoFragment -> navController.navigate(com.app.bustracking.R.id.infoFragment)
            com.app.bustracking.R.id.profileFragment -> navController.navigate(com.app.bustracking.R.id.profileFragment)
        }
    }

    private fun animateBottomNavigation(itemId: Int) {
        // Get the selected menu item view
        val view: View = binding.bottomNav.findViewById(itemId)

        // Create translation animations
        val translationY = ObjectAnimator.ofFloat(view, "translationY", 0f, -20f, 0f)

        // Combine the animations into a set
        val animatorSet = AnimatorSet()
        animatorSet.play(translationY)

        // Set the duration of the animations
        animatorSet.duration = 500

        // Start the animations
        animatorSet.start()
    }

    override fun onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(navigationReceiver)
        super.onDestroy()
    }

    companion object {
        var _binding: ActivityHomeBinding? = null

        @JvmStatic
        fun updateData(stopId: Int) {
            if (_binding != null) {
                StopsFragment.ARGMAIN = stopId
                _binding!!.bottomNav.selectedItemId = com.app.bustracking.R.id.stopsFragment
            }
        }
    }


}