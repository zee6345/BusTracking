package com.app.bustracking.presentation.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.app.bustracking.R
import com.app.bustracking.databinding.ActivityHomeBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fl_container_02) as NavHostFragment?
        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            setupWithNavController(binding.bottomNav, navController)
        }
    }
}