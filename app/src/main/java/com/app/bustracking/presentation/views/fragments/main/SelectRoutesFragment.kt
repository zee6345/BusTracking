package com.app.bustracking.presentation.views.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.databinding.FragmentSelectRoutesBinding

import com.app.bustracking.presentation.ui.SelectRouteAdapter
import com.app.bustracking.presentation.viewmodel.SelectRouteViewModel
import com.app.bustracking.presentation.views.activities.HomeActivity
import com.app.bustracking.presentation.views.fragments.BaseFragment

class SelectRoutesFragment : BaseFragment() {

    private lateinit var binding: FragmentSelectRoutesBinding
    private lateinit var navController: NavController
    private val data: SelectRouteViewModel by viewModels()

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data.fetchData()


        data.network.observe(viewLifecycleOwner) {

            binding.rvRoute.setHasFixedSize(true)
            binding.rvRoute.adapter = SelectRouteAdapter(it) { _, _ ->

                val intent = Intent(requireActivity(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }

        }
    }
}