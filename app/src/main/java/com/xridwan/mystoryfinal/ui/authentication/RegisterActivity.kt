package com.xridwan.mystoryfinal.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.xridwan.mystoryfinal.ViewModelFactory
import com.xridwan.mystoryfinal.data.Resource
import com.xridwan.mystoryfinal.databinding.ActivityRegisterBinding
import com.xridwan.mystoryfinal.ui.utils.hide
import com.xridwan.mystoryfinal.ui.utils.show
import com.xridwan.mystoryfinal.ui.utils.showToast

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: AuthViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        setView()

        binding.btnRegister.setOnClickListener {
            validate()
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

        ObjectAnimator.ofFloat(binding.imageView2, View.TRANSLATION_Y, -15F, 15F).apply {
            duration = 5000
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.textView4, View.ALPHA, 1f).setDuration(250)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(250)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(250)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.etName, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                title, nameEditTextLayout, emailEditTextLayout, passwordEditTextLayout, register
            )
            startDelay = 500
        }.start()
    }

    private fun validate() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val name = binding.etName.text.toString()

        when {
            name.isEmpty() -> {
                binding.etName.error = "Input Your Name"
                binding.etName.requestFocus()
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Input Your Email"
                binding.etEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Input your Password"
                binding.etPassword.requestFocus()
            }
            else -> {
                register()
            }
        }
    }

    private fun register() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        viewModel.register(name, email, password).observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.progressCircular.show()
                }
                is Resource.Success -> {
                    binding.progressCircular.hide()
                    showToast(response.data.message.toString())
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