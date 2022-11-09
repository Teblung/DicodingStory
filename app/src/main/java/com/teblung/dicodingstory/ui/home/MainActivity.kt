package com.teblung.dicodingstory.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.data.source.local.preference.DataStoreVM
import com.teblung.dicodingstory.databinding.ActivityMainBinding
import com.teblung.dicodingstory.ui.auth.login.LoginActivity
import com.teblung.dicodingstory.ui.home.upload.UploadActivity
import com.teblung.dicodingstory.ui.loading.LoadingAdapter
import com.teblung.dicodingstory.ui.maps.MapsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainVM by viewModels<MainVM>()
    private val dataStoreVM by viewModels<DataStoreVM>()
    private lateinit var adapterStory: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
                adapter = adapterStory.withLoadStateFooter(
                    footer = LoadingAdapter {
                        adapterStory.retry()
                    }
                )
            }
        }
    }

    private fun setupMain() {
        mainVM.apply {
            stories.observe(this@MainActivity) {
                if (it != null) {
                    adapterStory.submitData(lifecycle, it)
                    binding.apply {
                        rvStory.visibility = View.VISIBLE
                        tvWarning.visibility = View.GONE
                    }
                } else {
                    binding.apply {
                        rvStory.visibility = View.GONE
                        tvWarning.visibility = View.VISIBLE
                        tvWarning.text = getString(R.string.no_data)
                    }
                }
            }
            adapterStory.refresh()
        }
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
                dataStoreVM.setUserLogout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setupMain()
    }
}