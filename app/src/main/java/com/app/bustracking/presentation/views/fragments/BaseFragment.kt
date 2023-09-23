package com.app.bustracking.presentation.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.bustracking.R
import com.app.bustracking.data.preference.AppPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment: Fragment() {

    abstract fun initNavigation(navController: NavController)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = NavHostFragment.findNavController(this)
        initNavigation(controller)

        val isLogin = AppPreference.getBoolean("isLogin")
        if (isLogin){
//            controller.navigate(R.id.selectNetwrokFragment)
        }



    }
}