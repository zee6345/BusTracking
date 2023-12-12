package com.app.bustracking.presentation.views.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.data.local.RoutesDao
import com.app.bustracking.databinding.FragmentRoutesBinding
import com.app.bustracking.presentation.ui.RoutesAdapter
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Progress

const val ARGS = "data"
const val ARGS_LAT = "lat"
const val ARGS_LNG = "lng"

class RoutesFragment : BaseFragment() {

    private lateinit var binding: FragmentRoutesBinding
    private lateinit var navController: NavController

    private var isFavExpand = false
    private var isAllExpand = false
    private lateinit var progress: AlertDialog
    private lateinit var routeDao: RoutesDao

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


        routeDao = appDb().routesDao()

        val favouriteList = routeDao.fetchFavouriteRoutes()
        val routeList = routeDao.fetchRoutes()

        favouriteList.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {
                binding.rvFavorite.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            } else {
                binding.rvFavorite.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }

            //
            binding.rvFavorite.adapter = RoutesAdapter(it, routeDao) { route, position ->
                val args = Bundle()
                args.putInt(ARGS, route.routeId)
                navController.navigate(
                    R.id.action_routesFragment_to_routesMapFragment,
                    args,
                    navOptions()
                )
            }
        }

        routeList.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {
                binding.rvLines.visibility = View.VISIBLE
                binding.tvEmptyLines.visibility = View.GONE
            } else {
                binding.rvLines.visibility = View.GONE
                binding.tvEmptyLines.visibility = View.VISIBLE
            }

            binding.rvLines.adapter = RoutesAdapter(it, routeDao) { route, position ->
                val args = Bundle()
                args.putInt(ARGS, route.routeId)
                navController.navigate(
                    R.id.action_routesFragment_to_routesMapFragment,
                    args,
                    navOptions()
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

    override fun onDestroyView() {

        routeDao.fetchFavouriteRoutes().removeObservers(viewLifecycleOwner)
        routeDao.fetchRoutes().removeObservers(viewLifecycleOwner)

        super.onDestroyView()
    }

}
