package com.app.bustracking.presentation.views.fragments.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.data.responseModel.DataState
import com.app.bustracking.data.responseModel.GetAgenciesList
import com.app.bustracking.databinding.FragmentSelectNetwrokBinding
import com.app.bustracking.databinding.NoRouteDialogBinding
import com.app.bustracking.presentation.ui.SelectNetworkAdapter
import com.app.bustracking.presentation.viewmodel.AppViewModel
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Progress

private val TAG: String = SelectNetworkFragment::class.simpleName.toString()

class SelectNetworkFragment : BaseFragment() {

    private lateinit var binding: FragmentSelectNetwrokBinding
    private lateinit var navController: NavController

    //    private val data: SelectNetworkViewModel by viewModels()
    private val data: AppViewModel by viewModels()
    private lateinit var progress: AlertDialog


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

        progress = Progress(requireActivity()).showProgress()
        binding.rvNetwork.setHasFixedSize(true)

        //fetch data
        data.getAgencies()


        //update UI
        data.getAgenciesList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    progress.show()
                }

                is DataState.Error -> {
                    progress.dismiss()
                    Log.e(TAG, "onViewCreated: ${it.errorMessage}")
                }

                is DataState.Success -> {
                    progress.dismiss()

                    val agency = it.data as GetAgenciesList

                    binding.rvNetwork.adapter =
                        SelectNetworkAdapter(agency.agency_list) { obj, _ ->
                            val bundle = Bundle()
                            bundle.putInt("agent_id", obj.id)
                            navController.navigate(
                                R.id.action_selectNetwrokFragment_to_selectRoutesFragment,
                                bundle
                            )
                        }
                }

                else -> {

                }
            }


        }


    }




}