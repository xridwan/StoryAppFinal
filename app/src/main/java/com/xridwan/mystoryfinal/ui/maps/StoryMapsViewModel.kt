package com.xridwan.mystoryfinal.ui.maps

import androidx.lifecycle.ViewModel
import com.xridwan.mystoryfinal.data.StoryRepository

class StoryMapsViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    fun getStoriesWithLocation(location: Int, token: String) =
        repository.getStoriesWithLocation(location, token)
}