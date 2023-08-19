package com.app.bustracking.presentation.views.fragments.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentSelectNetwrokBinding
import com.app.bustracking.databinding.NoRouteDialogBinding

import com.app.bustracking.presentation.ui.SelectNetworkAdapter
import com.app.bustracking.presentation.viewmodel.SelectNetworkViewModel
import com.app.bustracking.presentation.views.fragments.BaseFragment

class SelectNetworkFragment : BaseFragment() {

    private lateinit var binding: FragmentSelectNetwrokBinding
    private lateinit var navController: NavController
    private val data: SelectNetworkViewModel by viewModels()

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectNetwrokBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data.fetchData()


        data.network.observe(viewLifecycleOwner) {

            binding.rvNetwork.setHasFixedSize(true)
            binding.rvNetwork.adapter = SelectNetworkAdapter(it) { _, position ->
                if (position == 2 || position == 4) {
                    showNoRouteDialog()
                } else {
                    navController.navigate(R.id.action_selectNetwrokFragment_to_selectRoutesFragment)
                }
            }

        }


    }

    private fun showNoRouteDialog() {
        val customDialog: View =
            LayoutInflater.from(requireActivity()).inflate(R.layout.no_route_dialog, null)
        val binding: NoRouteDialogBinding = NoRouteDialogBinding.bind(customDialog)
        val alert = AlertDialog.Builder(requireActivity())
        alert.setView(binding.root)
        val _dialog: AlertDialog = alert.create()
        _dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _dialog.show()

        binding.buttonDismiss.setOnClickListener {
            _dialog.dismiss()
        }

    }


}