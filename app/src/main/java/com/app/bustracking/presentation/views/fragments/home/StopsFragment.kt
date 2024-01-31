package com.app.bustracking.presentation.views.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.data.local.StopsDao

import com.app.bustracking.databinding.FragmentStopsBinding
import com.app.bustracking.presentation.ui.StopsAdapter
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Constants
import com.pixplicity.easyprefs.library.Prefs


class StopsFragment : BaseFragment() {

    private lateinit var binding: FragmentStopsBinding
    private lateinit var navController: NavController

    //    private val data: AppViewModel by viewModels()
//    private val sharedModel: SharedModel by viewModels()
    private var isFavExpand = false
    private var isAllExpand = false
    private lateinit var stopsDao: StopsDao
    private var agencyId: Int? = null
//    private lateinit var progress: AlertDialog


    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                navController.navigate(R.id.mapsFragment)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStopsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvLines.setHasFixedSize(true)
        binding.rvFavorite.setHasFixedSize(true)

        binding.toolbar.tvTitle.text = "Stops"
        binding.toolbar.ivSearch.visibility = View.GONE


        stopsDao = appDb.stopsDao()

        agencyId = Prefs.getInt(Constants.agencyId)
        val stops = stopsDao.fetchStops(agencyId!!)
        val favouriteStops = stopsDao.fetchFavouriteStops(agencyId!!)

        try {
            if (ARGMAIN != null) {
                val args = Bundle()
                args.putInt(ARGS, ARGMAIN!!)
                navController.navigate(
                    R.id.action_stopsFragment_to_stopsMapFragment,
                    args,
                    navOptions()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        favouriteStops.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.rvFavorite.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            } else {
                binding.rvFavorite.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }

            //
            binding.rvFavorite.adapter = StopsAdapter(it, stopsDao) { stop, position ->
                val args = Bundle()
                args.putInt(ARGS, stop.stopId)
                navController.navigate(
                    R.id.action_stopsFragment_to_stopsMapFragment,
                    args,
                    navOptions()
                )
            }
        }

        stops.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {
                binding.rvLines.visibility = View.VISIBLE
                binding.tvEmptyLines.visibility = View.GONE
            } else {
                binding.rvLines.visibility = View.GONE
                binding.tvEmptyLines.visibility = View.VISIBLE
            }


            binding.rvLines.adapter = StopsAdapter(it, stopsDao) { stop, position ->
                val args = Bundle()
                args.putInt(ARGS, stop.stopId)
                navController.navigate(
                    R.id.action_stopsFragment_to_stopsMapFragment,
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

    companion object {
        var ARGMAIN: Int? = null
    }

    override fun onDestroyView() {

        stopsDao.fetchStops(agencyId!!).removeObservers(viewLifecycleOwner)
        stopsDao.fetchFavouriteStops(agencyId!!).removeObservers(viewLifecycleOwner)

        super.onDestroyView()
    }

}