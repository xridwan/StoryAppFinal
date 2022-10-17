package com.xridwan.mystoryfinal.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.xridwan.mystoryfinal.data.local.StoryDatabase
import com.xridwan.mystoryfinal.data.network.ApiService
import com.xridwan.mystoryfinal.data.network.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun login(email: String, pass: String): LiveData<Resource<LoginResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.login(email, pass)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            Log.d("login", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String
    ): LiveData<Resource<RegisterResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.register(name, email, pass)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            Log.d("register", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
    }

    fun getStoriesWithLocation(location: Int, token: String): LiveData<Resource<StoryResponse>> =
        liveData {
            emit(Resource.Loading)
            try {
                val response = apiService.getStoriesWithLocation(location, token)
                emit(Resource.Success(response))
            } catch (e: Exception) {
                Log.d("story_maps", e.message.toString())
                emit(Resource.Error(e.message.toString()))
            }
        }

    fun postImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double,
        token: String,
        multiPort: String,
    ): LiveData<Resource<FileUploadResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.postImage(file, description, lat, lon, token, multiPort)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            Log.d("post_image", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }
}