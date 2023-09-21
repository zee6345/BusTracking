package com.app.bustracking.presentation.views.fragments.main

import android.content.Intent
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
import com.app.bustracking.data.preference.AppPreference
import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.requestModel.TravelRequest
import com.app.bustracking.data.responseModel.DataState
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import com.app.bustracking.databinding.FragmentSelectRoutesBinding
import com.app.bustracking.databinding.NoRouteDialogBinding

import com.app.bustracking.presentation.ui.SelectRouteAdapter
import com.app.bustracking.presentation.viewmodel.AppViewModel
import com.app.bustracking.presentation.views.activities.HomeActivity
import com.app.bustracking.presentation.views.fragments.BaseFragment

import com.app.bustracking.utils.Progress

private val TAG = SelectRoutesFragment::class.simpleName.toString()

class SelectRoutesFragment : BaseFragment() {

    private lateinit var binding: FragmentSelectRoutesBinding
    private lateinit var navController: NavController

    //    private val data: SelectRouteViewModel by viewModels()
    private val data: AppViewModel by viewModels()

    private lateinit var progress: AlertDialog

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

        progress = Progress(requireActivity()).showProgress()

        val agentId = requireArguments().getInt("agent_id") ?: 0
        binding.rvRoute.setHasFixedSize(true)

        data.getTravels(TravelRequest(agentId))


        data.getTravelList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                }

                is DataState.Error -> {
                    progress.dismiss()
                }

                is DataState.Success -> {
                    progress.dismiss()

                    val response = it.data as GetTravelList

                    if (response.travel_list.isEmpty()) {
                        showNoRouteDialog(true)
                    } else {
                        binding.rvRoute.adapter =
                            SelectRouteAdapter(response.travel_list) { travel, _ ->

                                //save id
                                AppPreference.putInt("agent_route_id", travel.id)

                                //network call
                                data.getTravelRouteList(RouteRequest(travel.id))
                            }
                    }
                }

                else -> {}
            }


        }


        data.getTravelRoutes.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                }

                is DataState.Error -> {
                    progress.dismiss()
                }

                is DataState.Success -> {
                    progress.dismiss()

                    val data = it.data as GetTravelRoutes

                    if (data.route_list.isEmpty()) {
                        showNoRouteDialog(false)
                    } else {

                        //start new activity
                        val intent = Intent(requireActivity(), HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }

                }

                else -> {}
            }

        }
    }

    private fun showNoRouteDialog(isBackEnable: Boolean) {
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

            if (isBackEnable) {
                navController.popBackStack()
            }
        }

    }
}