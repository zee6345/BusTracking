package com.app.bustracking.presentation.views.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentSignUpBinding
import com.app.bustracking.presentation.views.fragments.BaseFragment

class SignUpFragment : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignUpBinding

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignup.setOnClickListener {

            Toast.makeText(requireActivity(), "Register Successfully!", Toast.LENGTH_SHORT).show()

//            navController.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.tvLogin.setOnClickListener {
//            navController.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

    }
}