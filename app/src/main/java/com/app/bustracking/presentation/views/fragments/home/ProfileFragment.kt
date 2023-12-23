package com.app.bustracking.presentation.views.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.app.bustracking.BuildConfig
import com.app.bustracking.R
import com.app.bustracking.app.AppService
import com.app.bustracking.databinding.FragmentProfileBinding
import com.app.bustracking.presentation.views.activities.MainActivity

import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Constants
import com.app.bustracking.utils.Progress
import com.bumptech.glide.Glide
import com.pixplicity.easyprefs.library.Prefs


class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController

    private lateinit var progress: AlertDialog


    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.mapsFragment)
            }
        })

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

        val agencyName = Prefs.getString(Constants.agencyName)
        val agencyIcon = Prefs.getString(Constants.agencyIcon)


        binding.tvAgency.text = "v ${BuildConfig.VERSION_NAME}"
        binding.AgencyName.text = agencyName
        Glide.with(requireActivity())
            .load(agencyIcon)
            .into(binding.agencyIcon)



        progress = Progress(requireActivity()).showProgress(onCancel = {
            progress.dismiss()
        }, onExit = {

            //clear prefs
            Prefs.clear()
            Prefs.getAll().clear()


            requireActivity().stopService(Intent(requireActivity(), AppService::class.java))

            //exit app
            (requireActivity() as AppCompatActivity).finishAffinity()

        })


        binding.llChangeNetwork.setOnClickListener {

            //clear prefs
            Prefs.remove(Constants.agencyId)
            Prefs.remove(Constants.agencyName)


            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }

        binding.llLanguage.setOnClickListener {
            Toast.makeText(requireActivity(), "coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.llFavourite.setOnClickListener {
            Toast.makeText(requireActivity(), "coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.llInformation.setOnClickListener {
            navController.navigate(R.id.infoFragment)
        }

        binding.llLogout.setOnClickListener {
            progress.show()
        }

    }


}