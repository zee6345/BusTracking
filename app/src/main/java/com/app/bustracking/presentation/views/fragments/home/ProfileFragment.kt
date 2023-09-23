package com.app.bustracking.presentation.views.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.data.preference.AppPreference
import com.app.bustracking.databinding.FragmentProfileBinding
import com.app.bustracking.presentation.views.activities.MainActivity

import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Progress


class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController

    private lateinit var progress: AlertDialog


    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.tvTitle.text = "Profile"
        binding.toolbar.ivSearch.visibility = View.GONE

        progress = Progress(requireActivity()).showProgress(onCancel = {
            progress.dismiss()
        }, onExit = {
            AppPreference.clear()
            (requireActivity() as AppCompatActivity).finishAffinity()
        })


        binding.llChangeNetwork.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.llInformation.setOnClickListener {
            navController.navigate(R.id.infoFragment)
        }

        binding.llLogout.setOnClickListener {
            progress.show()
        }

    }


}