package com.xridwan.mystoryfinal.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.xridwan.mystoryfinal.R
import com.xridwan.mystoryfinal.data.network.response.ListStoryItem
import com.xridwan.mystoryfinal.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.toolbarLayout.title = title
        setDetail()
    }

    private fun setDetail() {
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA) as ListStoryItem

        binding.expandedImage.load(story.photoUrl) {
            crossfade(true)
            crossfade(500)
            placeholder(android.R.color.darker_gray)
            error(R.drawable.ic_placeholder)
        }
        binding.tvStoryName.text = story.name
        binding.tvDescription.text = story.description
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}