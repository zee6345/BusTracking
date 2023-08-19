package com.app.bustracking.presentation.views.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentLoginBinding
import com.app.bustracking.presentation.views.fragments.BaseFragment

class LoginFragment : BaseFragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var navController: NavController

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_selectNetwrokFragment)
        }

        binding.tvSignup.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_signUpFragment)
        }


    }
}