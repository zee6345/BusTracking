package com.app.bustracking.presentation.views.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.app.bustracking.R
import com.app.bustracking.databinding.ActivityHomeBinding
import com.app.bustracking.utils.SharedModel
import dagger.hilt.android.AndroidEntryPoint

private val TAG = HomeActivity::class.simpleName.toString()

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val sharedModel: SharedModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        val agentId = intent.getIntExtra("agent_route_id", 0)
//
//        sharedModel.agentId.value = agentId
//
//        Log.e(TAG, "onCreate: $agentId")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fl_container_02) as NavHostFragment?
        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            setupWithNavController(binding.bottomNav, navController)
        }
    }
}