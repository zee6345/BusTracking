package com.app.bustracking.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import com.app.bustracking.databinding.ActivityAuthBinding
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private val binding: ActivityAuthBinding by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //if already login route user to main screen
        val isLogin = Prefs.getBoolean("isLogin")
        if (isLogin) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}