package com.app.bustracking.presentation.views.fragments.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.bustracking.R
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.databinding.FragmentRouteMapModalSheetBinding
import com.app.bustracking.presentation.ui.RoutesMapAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RouteMapModalSheet(private val route: Route?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRouteMapModalSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteMapModalSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //show main view
        toggleViews(true)

        route?.apply {
            binding.tvTitle.text = route_title
            binding.lvMsg.visibility = if (description.isEmpty()) View.GONE else View.VISIBLE
            binding.tvText.text = description
        }

        binding.llRoute.setOnClickListener {
            binding.rvMapRoutes.visibility = View.VISIBLE
        }


        binding.rvMapRoutes.setHasFixedSize(true)
        binding.rvMapRoutes.adapter = RoutesMapAdapter(route!!.stop) { stop, position ->

            //show second view
            toggleViews(false)

            binding.tvTitle2.text = stop.stop_title

        }


    }

    private fun toggleViews(isRouteVisible: Boolean) {
        binding.llRoutes.visibility = if (isRouteVisible) View.VISIBLE else View.GONE
        binding.llRouteDetails.visibility = if (isRouteVisible) View.GONE else View.VISIBLE
    }

}