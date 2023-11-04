package com.app.bustracking.presentation.views.fragments.main

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
import com.app.bustracking.data.responseModel.Agency
import com.app.bustracking.data.responseModel.DataState
import com.app.bustracking.data.responseModel.GetAgenciesList
import com.app.bustracking.databinding.FragmentSelectNetwrokBinding
import com.app.bustracking.presentation.ui.SelectNetworkAdapter
import com.app.bustracking.presentation.viewmodel.AppViewModel
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Progress

private val TAG: String = SelectNetworkFragment::class.simpleName.toString()

class SelectNetworkFragment : BaseFragment() {

    private lateinit var binding: FragmentSelectNetwrokBinding
    private lateinit var navController: NavController
    private val data: AppViewModel by viewModels()
    private lateinit var progress: AlertDialog
    private val dataList = mutableListOf<Agency>()

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectNetwrokBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //fetch data
        data.getAgencies()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress = Progress(requireActivity()).showProgress()
        binding.rvNetwork.setHasFixedSize(true)
        val selectNetworkAdapter = SelectNetworkAdapter { obj, _ ->
            val bundle = Bundle()
            bundle.putInt("agent_id", obj.id)
            navController.navigate(
                R.id.action_selectNetwrokFragment_to_selectRoutesFragment, bundle
            )
        }
        binding.rvNetwork.adapter = selectNetworkAdapter


        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //ignore
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val filterList = ArrayList<Agency>()
                dataList.forEach {
                    if (it.name.lowercase().contains(p0.toString())) {
                        filterList.add(it)
                    }
                }

                selectNetworkAdapter.setList(filterList)

            }

            override fun afterTextChanged(p0: Editable?) {
                //ignore
            }

        })


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


                    dataList.apply {
                        clear()
                        addAll(agency.agency_list)
                    }

                    selectNetworkAdapter.setList(agency.agency_list)

                }

                else -> {

                }
            }


        }


    }


}