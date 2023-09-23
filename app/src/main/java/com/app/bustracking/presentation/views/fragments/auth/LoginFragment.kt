package com.app.bustracking.presentation.views.fragments.auth

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
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

class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
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

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {

            navController.navigate(R.id.action_loginFragment_to_verifyOTP)


            //navController.navigate(R.id.action_loginFragment_to_selectNetwrokFragment)
            val progressDialog = ProgressDialog(requireContext())
//            progressDialog.setTitle("Loading")
            progressDialog.setMessage("Loading...")

            val number = binding.phoneNumber.text.toString().trim()
            val code = binding.ccp.selectedCountryCode.toString().trim()

            if (TextUtils.isEmpty(number)) {
                binding.phoneNumber.error = " Please enter number"
            } else {
                progressDialog.show()
                val apiService = ApiClient.createService().create(ApiService::class.java)
                apiService.login("+$code$number").enqueue(object : Callback<LoginModel> {
                    override fun onResponse(
                        call: Call<LoginModel>,
                        response: Response<LoginModel>
                    ) {
                        progressDialog.dismiss()
                        if (response.isSuccessful && response.code() == 200) {
                            val model = response.body() as LoginModel
                            AppPreference.setToken(model.token)

                            val bundle = Bundle()
                            bundle.putString("number", "+$code$number")

                            navController.navigate(R.id.action_loginFragment_to_verifyOTP, bundle)

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to sign in!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                        progressDialog.dismiss()
                        t.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })

            }
        }

//        binding.tvSignup.setOnClickListener {
//            navController.navigate(R.id.action_loginFragment_to_signUpFragment)
//        }


    }
}