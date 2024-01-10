package com.app.bustracking.presentation.views.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentFavroutiesBinding
import com.app.bustracking.presentation.ui.RoutesAdapter
import com.app.bustracking.presentation.ui.StopsAdapter
import com.app.bustracking.presentation.views.fragments.BaseFragment

class FavroutiesFragment : BaseFragment() {

    private lateinit var binding: FragmentFavroutiesBinding
    private lateinit var navController: NavController
    private var isFavExpand = false
    private var isAllExpand = false

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                navController.navigate(R.id.mapsFragment)
                navController.popBackStack()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavroutiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val routeDao = appDb.routesDao()
        val stopsDao = appDb.stopsDao()
        val travelDao = appDb.travelDao()

        val favouriteRoutes = routeDao.fetchFavouriteRoutes()
        val favouriteStops = stopsDao.fetchAllFavouriteStops()

        binding.rlRoutes.setHasFixedSize(true)
        binding.rlStops.setHasFixedSize(true)


        val routesAdapter = RoutesAdapter(routeDao, travelDao) { route, position ->
            val args = Bundle()
            args.putInt(ARGS, route.routeId)
            navController.navigate(
                R.id.action_favroutiesFragment_to_routesMapFragment,
                args,
                navOptions()
            )
        }

        binding.rlRoutes.adapter = routesAdapter

        favouriteRoutes.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.rlRoutes.visibility = View.VISIBLE
                binding.tvRoutes.visibility = View.GONE
            } else {
                binding.rlRoutes.visibility = View.GONE
                binding.tvRoutes.visibility = View.VISIBLE
            }

            routesAdapter.updateList(it)
        }

        favouriteStops.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.rlStops.visibility = View.VISIBLE
                binding.tvStops.visibility = View.GONE
            } else {
                binding.rlStops.visibility = View.GONE
                binding.tvStops.visibility = View.VISIBLE
            }

            //
            binding.rlStops.adapter = StopsAdapter(it, stopsDao) { stop, position ->
                val args = Bundle()
                args.putInt(ARGS, stop.stopId)
                navController.navigate(
                    R.id.action_favroutiesFragment_to_stopsMapFragment,
                    args,
                    navOptions()
                )
            }
        }


        handleClickEvents()


    }

    private fun handleClickEvents() {
        binding.ivExpandFav.setOnClickListener {
            binding.rvRoutes.visibility = if (isFavExpand) View.VISIBLE else View.GONE
            isFavExpand = !isFavExpand
        }

        binding.ivExpandAll.setOnClickListener {
            binding.rvStops.visibility = if (isAllExpand) View.VISIBLE else View.GONE
            isAllExpand = !isAllExpand
        }

        binding.toolbar.tvTitle.text = "Favourites"
        binding.toolbar.ivSearch.visibility = View.GONE
    }
}