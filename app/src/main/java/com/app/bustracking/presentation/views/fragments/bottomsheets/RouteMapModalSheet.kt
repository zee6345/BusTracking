package com.app.bustracking.presentation.views.fragments.bottomsheets


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentRouteMapModalSheetBinding
import com.app.bustracking.presentation.model.Routes
import com.app.bustracking.presentation.ui.RoutesMapAdapter
import com.app.bustracking.presentation.viewmodel.RoutesMapViewModel

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class RouteMapModalSheet(private val route: Routes?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRouteMapModalSheetBinding
    private val data: RoutesMapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteMapModalSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(context!!, R.style.MyTransparentBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data.fetchData()

        //show main view
        toggleViews(true)

        route?.apply {
            binding.tvTitle.text = header
            binding.lvMsg.visibility = if (msg.isEmpty()) View.GONE else View.VISIBLE
            binding.tvText.text = msg
        }

        binding.llRoute.setOnClickListener {
            binding.rvMapRoutes.visibility = View.VISIBLE
        }

        data.routes.observe(viewLifecycleOwner) {

            binding.rvMapRoutes.setHasFixedSize(true)
            binding.rvMapRoutes.adapter = RoutesMapAdapter(it) { _, position ->

                //show second view
                toggleViews(false)

            }

        }

    }

    private fun toggleViews(isRouteVisible: Boolean) {
        binding.llRoutes.visibility = if (isRouteVisible) View.VISIBLE else View.GONE
        binding.llRouteDetails.visibility = if (isRouteVisible) View.GONE else View.VISIBLE
    }

}