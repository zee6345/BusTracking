package com.app.bustracking.presentation.views.activities


import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
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
    val navOptions: NavOptions = NavOptions.Builder()
        .setEnterAnim(com.app.bustracking.R.anim.slide_in)
        .setExitAnim(com.app.bustracking.R.anim.slide_out)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = binding
        setContentView(binding.root)

        //reset
        SelectNetworkFragment.isActivityLaunched = false

        navController = findNavController(com.app.bustracking.R.id.fl_container_02)
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        binding.motionLayout.transitionToStart()

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            binding.motionLayout.transitionToEnd()
            var destinationId = 0
            when (item.itemId) {
                com.app.bustracking.R.id.mapsFragment -> destinationId =
                    com.app.bustracking.R.id.mapsFragment

                com.app.bustracking.R.id.routesFragment -> destinationId =
                    com.app.bustracking.R.id.routesFragment

                com.app.bustracking.R.id.stopsFragment -> destinationId =
                    com.app.bustracking.R.id.stopsFragment

                com.app.bustracking.R.id.infoFragment -> destinationId =
                    com.app.bustracking.R.id.infoFragment

                com.app.bustracking.R.id.profileFragment -> destinationId =
                    com.app.bustracking.R.id.profileFragment
            }

            if (destinationId != 0) {
                navController.navigate(destinationId, Bundle(), navOptions)
                return@setOnNavigationItemSelectedListener true;
            } else {
                return@setOnNavigationItemSelectedListener false;
            }
        }

//        try {
//
//            navController = findNavController(R.id.fl_container_02)
//            binding.bottomNav.setupWithNavController(navController)
//
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

//        val navHostFragment = supportFragmentManager.findFragmentById(com.app.bustracking.R.id.fl_container_02) as NavHostFragment?
//        if (navHostFragment != null) {
//            navController = navHostFragment.navController
//            setupWithNavController(binding.bottomNav, navController)
//        }


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