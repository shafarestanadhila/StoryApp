package com.example.storyapp.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.model.StoryEntity
import com.example.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
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
    }

    private fun setupAction(){

        val data = intent.getParcelableExtra<StoryEntity>(STORY_EXTRA)

        val name = data?.name
        val description = data?.description
        val imgUrl = data?.photoUrl

        name?.let {
            binding.topAppBar.title = it
        }

        Glide.with(this)
            .load(imgUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailDescription.text = description
        binding.tvDetailName.text = name

    }

    companion object {
        const val STORY_EXTRA = "story_extra"
    }
}