package com.app.bustracking.presentation.views.fragments.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.app.bustracking.data.responseModel.Agency
import com.app.bustracking.data.responseModel.DataState
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import com.app.bustracking.data.responseModel.Travel
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
    private val data: AppViewModel by viewModels()
    private lateinit var progress: AlertDialog
    private var isSearchable = false
    private val dataList = mutableListOf<Travel>()

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

        //fetch api
        val agentId = requireArguments().getInt("agent_id") ?: 0
        data.getTravels(TravelRequest(agentId))


        progress = Progress(requireActivity()).showProgress()


        binding.rvRoute.setHasFixedSize(true)
        val adapter = SelectRouteAdapter { travel, _ ->
            //save id
            AppPreference.putInt("agent_route_id", travel.id)

            //network call
            data.getTravelRouteList(RouteRequest(travel.id))
        }
        binding.rvRoute.adapter = adapter


        binding.ivSearch.setOnClickListener {
            isSearchable = !isSearchable

            if (isSearchable){
                binding.searchBar.visibility = View.VISIBLE
            } else {
                binding.searchBar.visibility = View.GONE
            }
        }



        binding.searchView.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //ignore
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val filterList = ArrayList<Travel>()
                dataList.forEach {
                    if (it.travel_name.lowercase().contains(p0.toString())){
                        filterList.add(it)
                    }
                }

                adapter.setList(filterList)
            }

            override fun afterTextChanged(p0: Editable?) {
                //ignore
            }

        })


        //update ui
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

                        dataList.apply {
                            clear()
                            addAll(response.travel_list)
                        }

                        adapter.setList(response.travel_list)

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