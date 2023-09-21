package com.app.bustracking.presentation.views.fragments.main

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.data.api.ApiClient
import com.app.bustracking.data.api.ApiService
import com.app.bustracking.data.responseModel.VerifyOTPModel
import com.app.bustracking.databinding.FragmentVerifyOTPBinding
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.goodiebag.pinview.Pinview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VerifyOTPFragment  : BaseFragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            try{
                val argValue = it.getString("number")
                number = argValue!!
            } catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    lateinit var binding:FragmentVerifyOTPBinding
    lateinit var number:String


    private lateinit var navController: NavController

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyOTPBinding.inflate(layoutInflater,container,false)
//        return inflater.inflate(R.layout.fragment_verify_o_t_p, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pinview.setPinViewEventListener(object :Pinview.PinViewEventListener{
            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {

            }
        })


        binding.buttonLogin.setOnClickListener {

            //clear back stack
//            navController.navigate(R.id.action_loginFragment_to_selectNetwrokFragment)
            navController.navigate(R.id.action_verifyOTPFragment_to_selectNetwrokFragment)

            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Loading")
            val pin = binding.pinview.value
            if (TextUtils.isEmpty(pin)){
                binding.phoneNumber.error =" Please enter otp"
            }else{
                progressDialog.show()
                val apiService = ApiClient.createService().create(ApiService::class.java)
                apiService.verifyOTP(number,pin.toInt()).enqueue(object :Callback<VerifyOTPModel>{
                    override fun onResponse(
                        call: Call<VerifyOTPModel>,
                        response: Response<VerifyOTPModel>
                    ) {
                        progressDialog.dismiss()
                        if (response.code() == 200){
                            val model = response.body() as VerifyOTPModel
                            Toast.makeText(requireContext(),model.message,Toast.LENGTH_SHORT).show()

                            //clear back stack
                            navController.popBackStack(R.id.selectNetwrokFragment, false)

                            //navigate to new screen
                            navController.navigate(R.id.action_verifyOTPFragment_to_selectNetwrokFragment)
                        }else{
//                            val model = response.body() as VerifyOTPModel
                           Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<VerifyOTPModel>, t: Throwable) {
                        progressDialog.dismiss()
                        t.printStackTrace()
                        Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }


    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VerifyOTPFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}