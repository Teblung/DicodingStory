package com.teblung.dicodingstory.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.data.source.local.preference.SessionUser
import com.teblung.dicodingstory.databinding.ActivityMainBinding
import com.teblung.dicodingstory.ui.home.upload.UploadActivity
import com.teblung.dicodingstory.ui.login.LoginActivity
import com.teblung.dicodingstory.ui.maps.MapsActivity

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var preferences: SessionUser
    private lateinit var viewModel: MainViewModel
    private lateinit var adapterStory: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupPref()
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        supportActionBar?.title = getString(R.string.home)
        adapterStory = MainAdapter()
        binding.apply {
            tvMaps.text = getString(R.string.maps)
            tvMaps.setOnClickListener {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
            rvStory.apply {
                layoutManager = GridLayoutManager(this@MainActivity, 2)
                setHasFixedSize(true)
                adapter = adapterStory
            }
        }
    }

    private fun setupPref() {
        preferences = SessionUser(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                startActivity(Intent(this, UploadActivity::class.java))
            }
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.logout -> {
                preferences.setUserLogout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.apply {
            getAllStory(preferences.getLoginData().token)
            listStoryData.observe(this@MainActivity) {
                if (it != null) {
                    adapterStory.setStoryData(it)
                    binding.apply {
                        rvStory.visibility = View.VISIBLE
                        tvWarning.visibility = View.GONE
                    }
                } else {
                    binding.apply {
                        rvStory.visibility = View.GONE
                        tvWarning.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}