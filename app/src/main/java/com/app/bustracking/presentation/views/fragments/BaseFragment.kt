package com.app.bustracking.presentation.views.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.app.bustracking.R
import com.app.bustracking.app.AppService
import com.app.bustracking.data.local.Database
import com.app.bustracking.utils.Constants
import com.app.bustracking.utils.OnLocationReceive
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment(), OnLocationReceive {

    abstract fun initNavigation(navController: NavController)

    val appDb: Database by lazy {
        Database.init(requireActivity())
    }

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

        AppService.onLocationReceive = this@BaseFragment


//        if (!AppService.alreadyRunning) {
        //location service
        val agencyId = Prefs.getInt(Constants.agencyId)
        val routeDao = appDb.routesDao()
        val busId = routeDao.fetchBusId(agencyId)

        val locationIntent = Intent(requireActivity(), AppService::class.java)
        locationIntent.putExtra("bus_id", "${busId ?: 0}")
        requireActivity().startService(locationIntent)

//        }


    }


    fun navOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in)
            .setExitAnim(R.anim.slide_out)
//            .setPopEnterAnim(com.app.bustracking.R.anim.slide_in)
//            .setPopExitAnim(com.app.bustracking.R.anim.slide_out)
            .build()
    }

    fun showProgress(): AlertDialog {
        return AlertDialog.Builder(requireActivity(), R.style.TransparentAlertDialogTheme)
            .setView(R.layout.item_progress)
            .create()
    }

    fun showMessage(str: String) {
        Toast.makeText(requireActivity(), "$str", Toast.LENGTH_SHORT).show()
    }

    inline fun <reified T : Activity> routeScreen() {
        val intent = Intent(requireActivity(), T::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    override fun onLocationReceive(jsonData: String?) {
        //ignore
    }

}