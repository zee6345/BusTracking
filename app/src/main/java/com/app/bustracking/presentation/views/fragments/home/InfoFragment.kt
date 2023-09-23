package com.app.bustracking.presentation.views.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.app.bustracking.databinding.FragmentInfoBinding

import com.app.bustracking.presentation.views.fragments.BaseFragment

class InfoFragment : BaseFragment() {

    private lateinit var binding: FragmentInfoBinding
    private lateinit var navController: NavController

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.tvTitle.text = "Information"
        binding.toolbar.ivSearch.visibility = View.GONE

    }


}