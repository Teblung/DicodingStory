package com.teblung.dicodingstory.ui.home.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        supportActionBar?.title = getString(R.string.detail_story)
        setupData()
    }

    private fun setupData() {
        val img = intent.getStringExtra("IMAGE")
        val name = intent.getStringExtra("NAME")
        val desc = intent.getStringExtra("DESC")
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(img)
                .into(imgDetailStory)
            tvDetailName.text = name
            tvDetailDesc.text = desc
        }
    }
}