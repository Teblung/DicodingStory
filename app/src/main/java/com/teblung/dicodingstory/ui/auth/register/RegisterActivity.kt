package com.teblung.dicodingstory.ui.auth.register

import android.os.Bundle
import android.transition.Slide
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.databinding.ActivityRegisterBinding
import com.teblung.dicodingstory.ui.auth.AuthVM
import com.teblung.dicodingstory.ui.custom.InputView

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val authVM by viewModels<AuthVM>()
    private val character: Int = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            enterTransition = Slide()
        }
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            ivUsername.apply {
                setHint(resources.getString(R.string.username))
                setListener(object : InputView.InputViewListener {
                    override fun afterTextChanged(input: String) {
                        setCleartext(true)
                    }
                })
            }
            ivEmail.apply {
                setHint(resources.getString(R.string.email))
                setInputType(InputView.TYPE.EMAIL)
                setListener(object : InputView.InputViewListener {
                    override fun afterTextChanged(input: String) {
                        setCleartext(true)
                    }
                })
            }
            ivPassword.apply {
                setHint(resources.getString(R.string.password))
                setVisiblePassword()
                setInputType(InputView.TYPE.PASSWORD)
                setMinLength(6)
                setListener(object : InputView.InputViewListener {
                    override fun afterTextChanged(input: String) {

                    }
                })
            }
            btnRegister.apply {
                setText(R.string.register)
                setOnClickListener {
                    checkValidateRegister()
                }
            }
        }
    }

    private fun checkValidateRegister() {
        binding.apply {
            when {
                ivUsername.getText().isEmpty() -> {
                    ivUsername.setRequireField(resources.getString(R.string.req_field))
                }
                ivEmail.getText().isEmpty() -> {
                    ivEmail.setRequireField(resources.getString(R.string.req_field))
                }
                ivPassword.getText().isEmpty() -> {
                    ivPassword.setRequireField(resources.getString(R.string.req_field))
                }
                !ivEmail.isEmailValid(ivEmail.getText()) -> {
                    ivEmail.setRequireField(resources.getString(R.string.invalid_email))
                }
                ivPassword.getLength() < character -> {
                    ivPassword.setRequireField(
                        resources.getString(
                            R.string.req_password,
                            character.toString()
                        )
                    )
                }
                ivUsername.isValid() && ivEmail.isValid() && ivPassword.isValid() -> {
                    showMessage(getString(R.string.please_wait))
                    setupAuth()
                }
            }
        }
    }

    private fun setupAuth() {
        binding.apply {
            authVM.apply {
                loading.observe(this@RegisterActivity) {
                    showLoading(it)
                }
                message.observe(this@RegisterActivity) {
                    showMessage(it)
                }
                register(
                    name = ivUsername.getText(),
                    email = ivEmail.getText(),
                    password = ivPassword.getText()
                )
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            btnRegister.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showMessage(message: String) {
        when (message) {
            getString(R.string.bad_req) -> {
                binding.ivEmail.setRequireField(resources.getString(R.string.email_already_taken))
            }
            getString(R.string.user_created) -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish()
            }
            else -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}