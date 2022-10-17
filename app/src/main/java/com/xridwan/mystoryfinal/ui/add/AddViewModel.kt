package com.xridwan.mystoryfinal.ui.add

import androidx.lifecycle.ViewModel
import com.xridwan.mystoryfinal.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    fun postImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double,
        token: String,
        multiPort: String,
    ) = repository.postImage(file, description, lat, lon, token, multiPort)
}