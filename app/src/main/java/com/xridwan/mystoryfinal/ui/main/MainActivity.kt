package com.xridwan.mystoryfinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xridwan.mystoryfinal.R
import com.xridwan.mystoryfinal.ViewModelFactory
import com.xridwan.mystoryfinal.data.network.response.ListStoryItem
import com.xridwan.mystoryfinal.databinding.ActivityMainBinding
import com.xridwan.mystoryfinal.preferences.UserPreferences
import com.xridwan.mystoryfinal.ui.add.AddActivity
import com.xridwan.mystoryfinal.ui.authentication.LoginActivity
import com.xridwan.mystoryfinal.ui.detail.DetailActivity
import com.xridwan.mystoryfinal.ui.maps.StoryMapsActivity
import com.xridwan.mystoryfinal.ui.utils.hide

class MainActivity : AppCompatActivity(), StoryAdapter.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var preferences: UserPreferences
    private lateinit var factory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        preferences = UserPreferences(this)
        setStory()
    }

    private fun setStory() {
        val token = preferences.getToken() ?: ""
        getStory(token)
    }

    private fun getStory(token: String) {
        storyAdapter = StoryAdapter(this)
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.setHasFixedSize(true)
        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        val userToken = "Bearer $token"
        viewModel.getStory(userToken).observe(this) {
            storyAdapter.submitData(lifecycle, it)
            binding.progressCircular.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_map -> {
                startActivity(Intent(this, StoryMapsActivity::class.java))
                true
            }
            R.id.menu_add -> {
                startActivity(Intent(this, AddActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                dialogLogout()
                true
            }
            else -> true
        }
    }

    private fun dialogLogout() {
        AlertDialog.Builder(this).apply {
            setTitle("Logout")
            setMessage("Exit this app?")
            setCancelable(false)
            setPositiveButton("Ok") { _, _ ->
                preferences.setToken(null)
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    override fun onListener(story: ListStoryItem) {
        startActivity(
            Intent(this@MainActivity, DetailActivity::class.java)
                .putExtra(DetailActivity.EXTRA_DATA, story)
        )
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}