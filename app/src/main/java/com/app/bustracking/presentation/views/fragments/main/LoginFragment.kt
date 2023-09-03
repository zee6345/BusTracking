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
import com.app.bustracking.data.preference.AppPreference
import com.app.bustracking.data.responseModel.LoginModel
import com.app.bustracking.databinding.FragmentLoginBinding
import com.app.bustracking.presentation.views.fragments.BaseFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginFragment : BaseFragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var navController: NavController

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            //navController.navigate(R.id.action_loginFragment_to_selectNetwrokFragment)
        val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Loading")

            val number  = binding.phoneNumber.text.toString().trim()
            if (TextUtils.isEmpty(number)){
                binding.phoneNumber.error =" Please enter number"
            }else {
                progressDialog.show()
                val apiService = ApiClient.createService().create(ApiService::class.java)
                apiService.login(number).enqueue(object :Callback<LoginModel>{
                    override fun onResponse(
                        call: Call<LoginModel>,
                        response: Response<LoginModel>
                    ) {
                        progressDialog.dismiss()
                        if (response.isSuccessful && response.code() == 200){
                            val model = response.body() as LoginModel
                            AppPreference.setToken(model.token)

                            val bundle = Bundle()
                            bundle.putString("number", number)

                            navController.navigate(R.id.action_loginFragment_to_verifyOTP,bundle)

                        }else{
//                            val model = response.body() as LoginModel
                            Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                        progressDialog.dismiss()
                       t.printStackTrace()
                        Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                    }

                })

            }
        }

        binding.tvSignup.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_signUpFragment)
        }


    }
}