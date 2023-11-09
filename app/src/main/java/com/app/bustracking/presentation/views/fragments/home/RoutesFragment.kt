package com.app.bustracking.presentation.views.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentRoutesBinding
import com.app.bustracking.presentation.ui.RoutesAdapter
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Converter
import com.app.bustracking.utils.Progress

const val ARGS = "data"

class RoutesFragment : BaseFragment() {

    private lateinit var binding: FragmentRoutesBinding
    private lateinit var navController: NavController

    private var isFavExpand = false
    private var isAllExpand = false
    private lateinit var progress: AlertDialog

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

        progress = Progress(requireActivity()).showProgress()
        binding.rvLines.setHasFixedSize(true)

        binding.toolbar.tvTitle.text = "Routes"
        binding.toolbar.ivSearch.visibility = View.GONE


        val routesDao = appDb().routesDao()
        val stopsDao = appDb().stopsDao()
        val travelDao = appDb().travelDao()

        val travelList = travelDao.fetchTravels()

//        val data = Prefs.getString("travelList")
//        val routes = Converter.fromJson(data, GetTravelList::class.java)


        if (travelList.isNotEmpty()) {
            binding.rvLines.adapter = RoutesAdapter(travelList) { route, position ->
//                val json = Converter.toJson(route)
                val args = Bundle()
//                args.putString(ARGS, json)
                args.putInt(ARGS, route.travelId)
                navController.navigate(
                    R.id.action_routesFragment_to_routesMapFragment,
                    args
                )
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
