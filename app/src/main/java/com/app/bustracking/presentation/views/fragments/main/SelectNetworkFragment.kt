package com.app.bustracking.presentation.views.fragments.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.data.cache.AppDB
import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.requestModel.TravelRequest
import com.app.bustracking.data.responseModel.Agency
import com.app.bustracking.data.responseModel.DataState
import com.app.bustracking.data.responseModel.GetAgenciesList
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.FragmentSelectNetwrokBinding
import com.app.bustracking.presentation.ui.SelectNetworkAdapter
import com.app.bustracking.presentation.viewmodel.AppViewModel
import com.app.bustracking.presentation.views.activities.HomeActivity
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Constants
import com.app.bustracking.utils.Converter
import com.pixplicity.easyprefs.library.Prefs

private val TAG: String = SelectNetworkFragment::class.simpleName.toString()

class SelectNetworkFragment : BaseFragment() {

    private lateinit var binding: FragmentSelectNetwrokBinding
    private lateinit var navController: NavController
    private val data: AppViewModel by viewModels()
    private val dataList = mutableListOf<Agency>()
    private val stopsList = mutableListOf<Stop>()


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

        //if agency already selected, route to main
//        if (Prefs.getInt(Constants.agencyId) != 0) {
//            routeScreen<HomeActivity>()
//        }

        val dialog = showProgress()


        binding.rvNetwork.setHasFixedSize(true)
        val selectNetworkAdapter = SelectNetworkAdapter { obj, _ ->

            Prefs.putInt(Constants.agencyId, obj.id)
            Prefs.putString(Constants.agencyName, obj.name)

            //get travel list by agency id
            data.getTravels(TravelRequest(obj.id))

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
                    dialog.show()
                }

                is DataState.Error -> {
                    dialog.dismiss()
                    showMessage("something went wrong!")
                }

                is DataState.Success -> {
                    dialog.dismiss()

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

//        val travelEntity = getDb().travelDao()

        //for other
        //update ui
        data.getTravelList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    dialog.show()
                }

                is DataState.Error -> {
                    dialog.dismiss()
                }

                is DataState.Success -> {
                    dialog.dismiss()

                    val response = it.data as GetTravelList

                    //insert into db
//                    travelEntity.insertTravel(getTravelList)


//                    response.travel_list.forEach { travel ->
//                        travelEntity.insertTravel(travel)
//                    }


                    Prefs.putString(Constants.travelList, Converter.toJson(response)!!)

//                    fetch data for all travel lists
                    response.travel_list.forEach { travel ->
                        data.getTravelRouteList(RouteRequest(travel.id))
                    }
                }

                else -> {}
            }


        }

        data.getTravelRoutes.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    dialog.show()
                }

                is DataState.Error -> {
                    dialog.dismiss()
                }

                is DataState.Success -> {
                    dialog.dismiss()

                    val routesWithStops = it.data as GetTravelRoutes

                    writeToFile("routes.txt", routesWithStops.toString())

                    //success
                    routesWithStops.route_list.forEach { route ->

                        if (route.stop.isNotEmpty()) {
                            stopsList.addAll(route.stop)
                        }
                    }

                    if (stopsList.isNotEmpty()) {
                        //write data to file
                        writeToFile(
                            "stops.txt",
                            stopsList.toString()
                        )

                        //start new activity
                        routeScreen<HomeActivity>()
//                        requireActivity().finish()

                    } else {
                        showMessage("No route found for this Agency!")
                    }
                }

                else -> {}
            }

        }

    }


}