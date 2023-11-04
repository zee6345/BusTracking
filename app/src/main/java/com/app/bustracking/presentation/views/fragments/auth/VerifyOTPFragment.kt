package com.app.bustracking.presentation.views.fragments.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.app.bustracking.data.api.ApiClient
import com.app.bustracking.data.api.ApiService
import com.app.bustracking.data.preference.AppPreference
import com.app.bustracking.data.responseModel.VerifyOTPModel
import com.app.bustracking.databinding.FragmentVerifyOTPBinding
import com.app.bustracking.presentation.views.activities.MainActivity
import com.app.bustracking.presentation.views.fragments.BaseFragment

import com.goodiebag.pinview.Pinview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOTPFragment : BaseFragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


            try {
                val argValue = it.getString("number")
                number = argValue!!
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    lateinit var binding: FragmentVerifyOTPBinding
    lateinit var number: String


    private lateinit var navController: NavController

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyOTPBinding.inflate(layoutInflater, container, false)
//        return inflater.inflate(R.layout.fragment_verify_o_t_p, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pinview.setPinViewEventListener(object : Pinview.PinViewEventListener {
            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {

            }
        })

        binding.ivBack.setOnClickListener {
            navController.popBackStack()
        }


        binding.buttonLogin.setOnClickListener {

            //clear back stack
            AppPreference.putBoolean("isLogin", true)
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            (requireActivity() as AppCompatActivity).finish()


            //api call
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Loading")
            val pin = binding.pinview.value
            if (TextUtils.isEmpty(pin)) {
                binding.phoneNumber.error = " Please enter otp"
            } else {
                progressDialog.show()
                val apiService = ApiClient.createService().create(ApiService::class.java)
                apiService.verifyOTP(number, pin.toInt())
                    .enqueue(object : Callback<VerifyOTPModel> {
                        override fun onResponse(
                            call: Call<VerifyOTPModel>,
                            response: Response<VerifyOTPModel>
                        ) {
                            progressDialog.dismiss()
                            if (response.code() == 200) {
                                val model = response.body() as VerifyOTPModel
                                Toast.makeText(requireContext(), model.message, Toast.LENGTH_SHORT)
                                    .show()

                                //clear back stack
//                            navController.popBackStack(R.id.selectNetwrokFragment, false)
//
//                            //navigate to new screen
//                            navController.navigate(R.id.action_verifyOTPFragment_to_selectNetwrokFragment)
                            } else {
//                            val model = response.body() as VerifyOTPModel
                                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<VerifyOTPModel>, t: Throwable) {
                            progressDialog.dismiss()
                            t.printStackTrace()
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }

                    })
            }
        }


    }


}