package com.app.bustracking.presentation.views.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.app.bustracking.R
import com.app.bustracking.databinding.ActivityHomeBinding
import com.app.bustracking.presentation.views.fragments.home.MapsFragment
import com.app.bustracking.presentation.views.fragments.home.StopsFragment
import com.app.bustracking.presentation.views.fragments.main.SelectNetworkFragment
import dagger.hilt.android.AndroidEntryPoint

private val TAG = HomeActivity::class.simpleName.toString()

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private var route:Int = 0;

    private val navigationReceiver = object : BroadcastReceiver() {
        @SuppressLint("LogNotTimber")
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1?.apply {
                if (action == "com.app.navigate"){
                    val route = p1.getIntExtra("route", 0)
                    Log.e(TAG, "onReceive: $route")

//                    binding.bottomNav.selectedItemId = route

                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = binding
        setContentView(binding.root)

        //reset
        SelectNetworkFragment.isActivityLaunched = false


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fl_container_02) as NavHostFragment?
        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            setupWithNavController(binding.bottomNav, navController)


//            navController.addOnDestinationChangedListener { _, destination, _ ->
//                when (destination.id) {
//                    R.id.mapsFragment -> {
//                        binding.bottomNav.selectedItemId = R.id.mapsFragment
//                    }
//
//                    R.id.routesFragment -> {
//                        binding.bottomNav.selectedItemId = R.id.routesFragment
//                    }
//
//                    R.id.stopsMapFragment -> {
//                        binding.bottomNav.selectedItemId = R.id.stopsMapFragment
//                    }
//
//                    R.id.infoFragment -> {
//                        binding.bottomNav.selectedItemId = R.id.infoFragment
//                    }
//
//                    R.id.profileFragment -> {
//                        binding.bottomNav.selectedItemId = R.id.profileFragment
//                    }
//                }
//            }


            val filter = IntentFilter("com.app.navigate")
            LocalBroadcastManager.getInstance(this).registerReceiver(navigationReceiver, filter)

        }


    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(navigationReceiver)
        super.onDestroy()
    }

    companion object {
        var _binding:ActivityHomeBinding?=null
        @JvmStatic
        fun updateData(stopId:Int) {
            if (_binding!=null){
                StopsFragment.ARGMAIN = stopId
                _binding!!.bottomNav.selectedItemId = R.id.stopsFragment
            }
        }
    }


}