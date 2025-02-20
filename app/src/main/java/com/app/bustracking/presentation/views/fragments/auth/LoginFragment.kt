package com.app.bustracking.presentation.views.fragments.auth

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.app.bustracking.R
import com.app.bustracking.data.api.ApiClient
import com.app.bustracking.data.api.ApiService
import com.app.bustracking.data.preference.AppPreference
import com.app.bustracking.data.responseModel.LoginModel
import com.app.bustracking.databinding.FragmentLoginBinding
import com.app.bustracking.presentation.views.activities.MainActivity
import com.app.bustracking.presentation.views.fragments.BaseFragment
import com.app.bustracking.utils.Constants
import com.pixplicity.easyprefs.library.Prefs
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

        val dialog = showProgress()

        /*
         *  if already login route to main activity
         */
        if (Prefs.getBoolean(Constants.isLogin)) {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            (requireActivity() as AppCompatActivity).finish()
        }

        //animate
        animateImageView(binding.imageView)


        binding.buttonLogin.setOnClickListener {

//            val progressDialog = ProgressDialog(requireContext())
//            progressDialog.setMessage("Loading...")

            val number = binding.phoneNumber.text.toString().trim()
            val code = binding.ccp.selectedCountryCode.toString().trim()

            if (TextUtils.isEmpty(number)) {
                binding.phoneNumber.error = " Please enter number"
            } else {
                dialog.show()

                val apiService = ApiClient.createService().create(ApiService::class.java)
                apiService.login("+$code$number").enqueue(object : Callback<LoginModel> {
                    override fun onResponse(
                        call: Call<LoginModel>,
                        response: Response<LoginModel>
                    ) {
                        dialog.dismiss()
                        if (response.isSuccessful && response.code() == 200) {

                            try {

                                val model = response.body() as LoginModel
                                AppPreference.setToken(model.token)

                                //
                                Prefs.putBoolean(Constants.isLogin, true)

                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                startActivity(intent)
                                (requireActivity() as AppCompatActivity).finish()

                            } catch (e: Exception) {

                                val bundle = Bundle()
                                bundle.putString("number", "+$code$number")

                                navController.navigate(
                                    R.id.action_loginFragment_to_verifyOTP,
                                    bundle
                                )
                            }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to sign in!",
                                Toast.LENGTH_SHORT
                            ).show()

                            Prefs.putBoolean(Constants.isLogin, false)
                        }
                    }

                    override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                        dialog.dismiss()

                        t.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong!",
                            Toast.LENGTH_SHORT
                        ).show()

                        Prefs.putBoolean(Constants.isLogin, false)
                    }

                })

            }
        }

    }

    private fun animateImageView(imageView: ImageView) {
//        val translateAnimator = ObjectAnimator.ofFloat(imageView, "translationY", 0f, 200f)
//        translateAnimator.duration = 600 // Animation duration in milliseconds
////        translateAnimator.repeatCount = ValueAnimator.INFINITE // Repeat the animation infinitely
//        translateAnimator.repeatCount = 1 // Repeat the animation infinitely
//        translateAnimator.repeatMode = ValueAnimator.REVERSE // Reverse the animation on each repeat
//
//        // Start the animation
//        translateAnimator.start()

//        val screenWidth = resources.displayMetrics.widthPixels
//
//        // Create an ObjectAnimator for translationX animation
//        val translateAnimator = ObjectAnimator.ofFloat(imageView, "translationX", -screenWidth.toFloat(), screenWidth.toFloat())
//        translateAnimator.duration = 3000 // Animation duration in milliseconds
//
//        // Start the animation
//        translateAnimator.start()


        val slideInAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_in_from_left)
        imageView.startAnimation(slideInAnimation)

    }
}