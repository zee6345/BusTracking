package com.app.bustracking.presentation.views.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.databinding.FragmentRoutesBinding

import com.app.bustracking.presentation.ui.RoutesAdapter
import com.app.bustracking.presentation.viewmodel.RoutesViewModel
import com.app.bustracking.presentation.views.fragments.BaseFragment

const val ARGS = "data"
class RoutesFragment : BaseFragment() {

    private lateinit var binding: FragmentRoutesBinding
    private lateinit var navController: NavController
    private val data: RoutesViewModel by viewModels()
    private var isFavExpand = false
    private var isAllExpand = false

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


        data.fetchData()


        data.routes.observe(viewLifecycleOwner) {
            print(it.toString())

            binding.rvLines.setHasFixedSize(true)
            binding.rvLines.adapter = RoutesAdapter(it) { route, position ->

                val args = Bundle()
                args.putSerializable(ARGS, route)
                navController.navigate(R.id.action_routesFragment_to_routesMapFragment, args)

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
