package com.xridwan.mystoryfinal.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.xridwan.mystoryfinal.ViewModelFactory
import com.xridwan.mystoryfinal.data.Resource
import com.xridwan.mystoryfinal.databinding.ActivityLoginBinding
import com.xridwan.mystoryfinal.preferences.UserPreferences
import com.xridwan.mystoryfinal.ui.main.MainActivity
import com.xridwan.mystoryfinal.ui.utils.hide
import com.xridwan.mystoryfinal.ui.utils.show
import com.xridwan.mystoryfinal.ui.utils.showToast

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferences: UserPreferences
    private lateinit var factory: ViewModelFactory
    private val viewModel: AuthViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setupViewModel()
        getSession()

        binding.btnLogin.setOnClickListener {
            validate()
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
        preferences = UserPreferences(this)
    }

    private fun getSession() {
        if (!preferences.getToken().isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java).also {
                finish()
            })
        }
    }

    private fun validate() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        when {
            email.isEmpty() -> {
                binding.etEmail.error = "Input Your Email"
                binding.etEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Input Your Password"
                binding.etPassword.requestFocus()
            }
            else -> {
                login()
            }
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        viewModel.login(email, password).observe(this) { response ->
            if (response != null) {
                when (response) {
                    is Resource.Loading -> {
                        binding.progressCircular.show()
                    }
                    is Resource.Success -> {
                        binding.progressCircular.hide()
                        showToast(response.data.message)
                        val data = response.data
                        preferences.setToken(data.loginResult.token)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra(MainActivity.EXTRA_DATA, data.loginResult.token)
                        startActivity(intent)
                        finish()
                    }
                    is Resource.Error -> {
                        binding.progressCircular.hide()
                        showToast(response.error)
                    }
                }
            }
        }
    }

    private fun setView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        ObjectAnimator.ofFloat(binding.loginImage, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 5000
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(250)
        val subTitle = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(250)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(250)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(250)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                title,
                subTitle,
                emailEditTextLayout,
                passwordEditTextLayout,
                login,
                register
            )
            startDelay = 500
        }.start()
    }
}