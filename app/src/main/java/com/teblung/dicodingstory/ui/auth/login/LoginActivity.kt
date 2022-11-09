package com.teblung.dicodingstory.ui.auth.login

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.data.source.local.preference.DataStoreVM
import com.teblung.dicodingstory.data.source.local.preference.User
import com.teblung.dicodingstory.databinding.ActivityLoginBinding
import com.teblung.dicodingstory.ui.auth.AuthVM
import com.teblung.dicodingstory.ui.auth.register.RegisterActivity
import com.teblung.dicodingstory.ui.custom.InputView
import com.teblung.dicodingstory.ui.home.MainActivity

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val authVM by viewModels<AuthVM>()
    private val dataStoreVM by viewModels<DataStoreVM>()
    private val character: Int = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
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
            btnLogin.apply {
                setText(R.string.login)
                setOnClickListener {
                    checkValidateLogin()
                }
            }
            tvOr.text = resources.getString(R.string.or)
            btnRegister.apply {
                setText(R.string.register)
                setOnClickListener {
                    startActivity(
                        Intent(this@LoginActivity, RegisterActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@LoginActivity).toBundle()
                    )
                }
            }
        }
    }

    private fun checkValidateLogin() {
        binding.apply {
            when {
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
                ivEmail.isValid() && ivPassword.isValid() -> {
                    showMessage(getString(R.string.please_wait))
                    setupAuth()
                }
            }
        }
    }

    private fun setupAuth() {
        binding.apply {
            authVM.apply {
                loading.observe(this@LoginActivity) {
                    showLoading(it)
                }
                message.observe(this@LoginActivity) {
                    showMessage(it)
                }
                login(
                    email = ivEmail.getText(),
                    password = ivPassword.getText()
                )
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            btnLogin.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showMessage(message: String) {
        when (message) {
            getString(R.string.unauthorized) -> {
                Toast.makeText(this, getString(R.string.email_password), Toast.LENGTH_SHORT).show()
            }
            getString(R.string.success) -> {
                authVM.userLogin.observe(this@LoginActivity) {
                    val currentUser = User(
                        it.name,
                        it.token,
                        it.userId,
                        true
                    )
                    dataStoreVM.setUserLogin(currentUser)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
            else -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting, menu)
        if (menu != null) {
            menu.findItem(R.id.add).isVisible = false
            menu.findItem(R.id.logout).isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}