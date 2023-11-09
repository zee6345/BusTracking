package com.app.bustracking.presentation.views.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.FragmentRoutesBinding
import com.app.bustracking.presentation.ui.RoutesAdapter
import com.app.bustracking.presentation.viewmodel.AppViewModel
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Constants
import com.app.bustracking.utils.Converter
import com.app.bustracking.utils.SharedModel
import com.pixplicity.easyprefs.library.Prefs

const val ARGS = "data"

private val TAG = RoutesFragment::class.simpleName.toString()

class RoutesFragment : BaseFragment() {

    private lateinit var binding: FragmentRoutesBinding
    private lateinit var navController: NavController

    private val data: AppViewModel by viewModels()
    private val sharedModel: SharedModel by viewModels()
    private var isFavExpand = false
    private var isAllExpand = false
//    private lateinit var progress: AlertDialog

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val progress = showProgress()
        binding.rvLines.setHasFixedSize(true)

        binding.toolbar.tvTitle.text = "Routes"
        binding.toolbar.ivSearch.visibility = View.GONE


        val data = Prefs.getString(Constants.travelList)
        val routes = Converter.fromJson(data, GetTravelList::class.java)

//        val routeWithStop = Prefs.getString(Constants.travelRoute)
//        val travelRoute = Converter.fromJson(routeWithStop, GetTravelRoutes::class.java)

//        travelRoute.route_list.forEach {
//            Log.e("mTAGsdaskd", it.toString())
//        }


//        val stopsList = parseStopsFromString(readFromFile("${requireActivity().filesDir}/stops.txt"))


        if (routes.travel_list.isNotEmpty()) {


            binding.rvLines.adapter = RoutesAdapter(routes.travel_list) { route, position ->




                Log.e("mTAG", "${route.toString()}")



                val temp = ArrayList<Stop>()

//                stopsList.forEach {
//                    if (it.agency_id == route.agency_id){
//                        temp.add(it)
//                    }
//                }

//                Log.e("mTAG", "$temp")

                val json = Converter.toJson(route)
//                AppPreference.putString("route", json.toString())

//                val args = Bundle()
//                args.putString(ARGS, json)
//                navController.navigate(
//                    R.id.action_routesFragment_to_routesMapFragment,
//                    args
//                )
            }

        }


        binding.ivExpandFav.setOnClickListener {
            binding.rlFav.visibility = if (isFavExpand) View.VISIBLE else View.GONE
            isFavExpand = !isFavExpand
        }

        binding.ivExpandAll.setOnClickListener {
            binding.rlAll.visibility = if (isAllExpand) View.VISIBLE else View.GONE
            isAllExpand = !isAllExpand
        }


    }


}
