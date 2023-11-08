package com.app.bustracking.presentation.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.bustracking.data.api.ApiClient
import com.app.bustracking.data.api.ApiService
import com.app.bustracking.data.preference.AppPreference
import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.requestModel.TravelRequest
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.data.responseModel.Travel
import com.app.bustracking.db.RoomDB
import com.app.bustracking.db.entities.RouteEntity
import com.app.bustracking.db.entities.TravelEntity
import com.app.bustracking.db.entities.TravelRouteRelation
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    abstract fun initNavigation(navController: NavController)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = NavHostFragment.findNavController(this)
        initNavigation(controller)

        val isLogin = AppPreference.getBoolean("isLogin")
        if (isLogin) {
//            controller.navigate(R.id.selectNetwrokFragment)
        }

        callAllAPICache()

    }

    var travelList: GetTravelList? = null

    @SuppressLint("LogNotTimber")
    private fun callAllAPICache() {
        val getSavedAgency = AppPreference.getSavedAgency()
        val apiService = ApiClient.createService().create(ApiService::class.java)
        val call = apiService.getTravelListMain(TravelRequest(getSavedAgency))
        call.enqueue(object : Callback<GetTravelList> {
            override fun onResponse(
                call: Call<GetTravelList>,
                response: Response<GetTravelList>
            ) {
                if (response.isSuccessful) {
                    travelList = response.body()
                    viewLifecycleOwner.lifecycleScope.launch {
                        callSecondAPi(apiService)
                    }
                }
            }

            override fun onFailure(call: Call<GetTravelList>, t: Throwable) {
                Log.e("BASE Fragment", "onViewCreated: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private suspend fun callSecondAPi(apiService: ApiService) {
        if (travelList != null) {
            val list = travelList!!.travel_list
            val arrayList = ArrayList<Int>()
            for (i in list.indices) {
                arrayList.add(list[i].id)
            }

            val deferredResponses = arrayList.map { id ->
                lifecycleScope.async {
                    apiService.getTravelRoutesMain(RouteRequest(id))
                }
            }


            val responses = deferredResponses.mapNotNull {
                try {
                    it.await()
                } catch (e: Exception) {
                    // Handle API call failure (e.g., log the error)
                    e.printStackTrace()
                    null
                }
            }
            val apiResponses = responses.map { response ->
                //ApiResponseEntity(response.id, response.data)
                saveTravelAndRouteData(travelList!!.travel_list, response.body()!!.route_list)
            }
        }
    }

    suspend fun saveTravelAndRouteData(apiResponses: List<Travel>, routeList: List<Route>) {
        val database = RoomDB.getInstance(requireContext())
        val travelDao = database!!.travelDao()
        val routeDao = database.routeDao()
        val travelRouteRelationDao = database.travelRouteRelationDao()
        apiResponses.forEach { response ->
            val travelEntity = TravelEntity(
                response.id,
                response.travel_name,
                response.travel_description,
                response.agency_id,
                response.bus_id,
                response.bus_number_plate,
                response.created_at,
                response.driver_id,
                response.matricule,
                response.travel_arrival_time,
                response.travel_departure_time,
                response.trip_id,
                response.updated_at,
                response.user_id
            )
            val travelId = travelDao.insertTravel(travelEntity)

            val routeIds = mutableListOf<Long>()
            // Save 'route' data to the 'route_list' table
            routeList.forEach { routeResponse ->

                val routeEntity = RouteEntity(
                    routeResponse.id,
                    routeResponse.route_title,
                    routeResponse.description,
                    routeResponse.agency_id,
                    routeResponse.bus_id,
                    routeResponse.color,
                    routeResponse.created_at,
                    routeResponse.direction_id,
                    routeResponse.latitude,
                    routeResponse.longitude,
                    routeResponse.stop,
                    routeResponse.travel_id,
                    routeResponse.trip_distance,
                    routeResponse.type,
                    routeResponse.updated_at,
                )

                val routeId = routeDao.insertRoute(routeEntity)
                routeIds.add(routeId)

                routeIds.forEach { routeIda ->
                    val travelRouteRelation = TravelRouteRelation(
                        0,
                        travelId.toInt(), // Convert to Int since Room returns Long for insert
                        routeIda.toInt()   // Convert to Int since Room returns Long for insert
                    )
                    travelRouteRelationDao.insertRelation(travelRouteRelation)
                }
            }

        }
    }
}