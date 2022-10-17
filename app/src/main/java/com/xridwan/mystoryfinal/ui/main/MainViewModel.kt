package com.xridwan.mystoryfinal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xridwan.mystoryfinal.data.StoryRepository
import com.xridwan.mystoryfinal.data.network.response.ListStoryItem

class MainViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> =
        repository.getStories(token).cachedIn(viewModelScope)
}