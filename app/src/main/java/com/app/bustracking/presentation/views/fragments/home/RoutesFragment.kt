package com.app.bustracking.presentation.views.fragments.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.app.AppService
import com.app.bustracking.data.local.RoutesDao
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.databinding.FragmentRoutesBinding
import com.app.bustracking.presentation.ui.RoutesAdapter
import com.app.bustracking.presentation.views.fragments.BaseFragment
import org.json.JSONException
import org.json.JSONObject

const val ARGS = "data"
const val ARGS_LAT = "lat"
const val ARGS_LNG = "lng"

const val TAG = "mmTAG"

class RoutesFragment : BaseFragment() {

    private lateinit var binding: FragmentRoutesBinding
    private lateinit var navController: NavController

    private var isFavExpand = false
    private var isAllExpand = false
    private lateinit var lineAdapter: RoutesAdapter
    private lateinit var favAdapter: RoutesAdapter
    private lateinit var routeDao: RoutesDao

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AppService.RECEIVER_ACTION) {
                val jsonData = intent.getStringExtra(AppService.RECEIVER_DATA)

                if (!jsonData.isNullOrEmpty()) {
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val data = jsonObject.getString("data")
                        val location = JSONObject(data).getString("location")
                        val lat = JSONObject(location).getDouble("lat")
                        val lon = JSONObject(location).getDouble("long")
                        val busId = JSONObject(location).getInt("bus_id")

                        //update vehicle status in database
                        val route = routeDao.fetchRouteByBusId(busId)
                        route.forEach {
                            it.isVehicleConnected = true
                            routeDao.updateVehicleStatus(it)
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()

        requireActivity().registerReceiver(
            broadcastReceiver,
            IntentFilter(AppService.RECEIVER_ACTION)
        )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        routeDao = appDb().routesDao()
        val favouriteRoutes = routeDao.fetchFavouriteRoutes()
        val routesList = routeDao.fetchRoutes()


        favAdapter = RoutesAdapter(routeDao) { route, position ->
            val args = Bundle()
            args.putInt(ARGS, route.routeId)
            navController.navigate(
                R.id.action_routesFragment_to_routesMapFragment,
                args,
                navOptions()
            )
        }

        lineAdapter = RoutesAdapter(routeDao) { route, position ->
            val args = Bundle()
            args.putInt(ARGS, route.routeId)
            navController.navigate(
                R.id.action_routesFragment_to_routesMapFragment,
                args,
                navOptions()
            )
        }


        binding.rvLines.adapter = lineAdapter
        binding.rvFavorite.adapter = favAdapter


        updateUI(favouriteRoutes, routesList)


        handleClickEvents()


    }

    private fun handleClickEvents() {
        binding.ivExpandFav.setOnClickListener {
            binding.rlFav.visibility = if (isFavExpand) View.VISIBLE else View.GONE
            isFavExpand = !isFavExpand
        }

        binding.ivExpandAll.setOnClickListener {
            binding.rlAll.visibility = if (isAllExpand) View.VISIBLE else View.GONE
            isAllExpand = !isAllExpand
        }
    }

    private fun initData() {
        binding.toolbar.tvTitle.text = "Routes"
        binding.toolbar.ivSearch.visibility = View.GONE

        binding.rvLines.setHasFixedSize(true)
        binding.rvFavorite.setHasFixedSize(true)
    }

    private fun updateUI(
        favouriteRoutes: LiveData<MutableList<Route>>,
        routesList: LiveData<MutableList<Route>>
    ) {

        favouriteRoutes.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.rvFavorite.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            } else {
                binding.rvFavorite.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }

            favAdapter.updateList(it)
        }

        routesList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.rvLines.visibility = View.VISIBLE
                binding.tvEmptyLines.visibility = View.GONE
            } else {
                binding.rvLines.visibility = View.GONE
                binding.tvEmptyLines.visibility = View.VISIBLE
            }

            lineAdapter.updateList(it)
        }
    }

    override fun onDestroy() {


//        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)

        super.onDestroy()
    }

}
