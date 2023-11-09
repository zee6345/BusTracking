package com.app.bustracking.presentation.views.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentStopsAllBinding
import com.app.bustracking.presentation.ui.StopsAdapter
import com.app.bustracking.presentation.views.fragments.BaseFragment

class StopsFragment : BaseFragment() {

    private lateinit var binding: FragmentStopsAllBinding
    private lateinit var navController: NavController

    //    private val data: AppViewModel by viewModels()
//    private val sharedModel: SharedModel by viewModels()
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
        binding = FragmentStopsAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progress = showProgress()
        binding.rvLines.setHasFixedSize(true)

        binding.toolbar.tvTitle.text = "Stops"
        binding.toolbar.ivSearch.visibility = View.GONE

        val routesDao = appDb().routesDao()
        val stopsDao = appDb().stopsDao()
        val travelDao = appDb().travelDao()


//        val data = readFromFile(requireActivity().filesDir.absolutePath + "/stops.txt")
//        val stopsList = parseStopsFromString(data)

        val stops = stopsDao.fetchStops()


        if (stops.isNotEmpty()) {
            binding.rvLines.adapter = StopsAdapter(stops) { stop, position ->
//                val json = Converter.toJson(route)
                val args = Bundle()
//                args.putString(ARGS, json)
                args.putInt(ARGS, stop.stopId)
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