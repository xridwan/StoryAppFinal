package com.xridwan.mystoryfinal.data

import com.xridwan.mystoryfinal.DataDummy
import com.xridwan.mystoryfinal.data.network.ApiService
import com.xridwan.mystoryfinal.data.network.response.FileUploadResponse
import com.xridwan.mystoryfinal.data.network.response.LoginResponse
import com.xridwan.mystoryfinal.data.network.response.RegisterResponse
import com.xridwan.mystoryfinal.data.network.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {

    private val dummyRegisterResponse = DataDummy.dummyRegisterResponse()
    private val dummyLoginResponse = DataDummy.dummyLoginResponse()
    private val dummyStoryResponse = DataDummy.dummyStoryWithLocationResponse()
    private val dummyAddNewStoryResponse = DataDummy.dummyAddStoryResponse()

    override suspend fun login(email: String, password: String): LoginResponse {
        return dummyLoginResponse
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        return dummyRegisterResponse
    }

    override suspend fun getStories(page: Int, size: Int, token: String): StoryResponse {
        return dummyStoryResponse
    }

    override suspend fun getStoriesWithLocation(loc: Int, token: String): StoryResponse {
        return dummyStoryResponse
    }

    override suspend fun postImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?,
        token: String,
        type: String,
    ): FileUploadResponse {
        return dummyAddNewStoryResponse
    }
}