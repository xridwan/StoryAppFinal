package com.xridwan.mystoryfinal.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.xridwan.mystoryfinal.DataDummy
import com.xridwan.mystoryfinal.MainDispatcherRule
import com.xridwan.mystoryfinal.data.network.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRules = MainDispatcherRule()

    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        apiService = FakeApiService()
    }

    @Test
    fun getStoriesTest() = runTest {
        val expectedStory = DataDummy.dummyStoryResponse()
        val actualStory = apiService.getStories(1, 50, TOKEN)
        assertNotNull(actualStory)
        assertEquals(expectedStory.size, actualStory.listStory.size)
    }

    @Test
    fun registerTest() = runTest {
        val expectedUser = DataDummy.dummyRegisterResponse()
        val actualUser = apiService.register(NAME, EMAIL, PASSWORD)
        assertNotNull(actualUser)
        assertEquals(expectedUser.message, actualUser.message)
    }

    @Test
    fun loginTest() = runTest {
        val expectedUser = DataDummy.dummyLoginResponse()
        val actualUser = apiService.login(EMAIL, PASSWORD)
        assertNotNull(actualUser)
        assertEquals(expectedUser.message, actualUser.message)
    }

    @Test
    fun getStoryWithLocationText() = runTest {
        val expectedStory = DataDummy.dummyStoryWithLocationResponse()
        val actualStory = apiService.getStoriesWithLocation(1, TOKEN)
        assertNotNull(actualStory)
        assertEquals(expectedStory.listStory.size, actualStory.listStory.size)
    }

    @Test
    fun postImage() = runTest {
        val description = "Ini adalah deksripsi gambar".toRequestBody("text/plain".toMediaType())

        val file = mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "nameFile",
            requestImageFile
        )

        val expectedStory = DataDummy.dummyAddStoryResponse()
        val actualStory = apiService.postImage(imageMultipart, description, LAT, LON, TOKEN, ACCEPT)
        assertNotNull(actualStory)
        assertEquals(expectedStory.message, actualStory.message)
    }

    companion object {
        private const val TOKEN = "Bearer TOKEN"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val LAT = 1.00
        private const val LON = 1.00
        private const val ACCEPT = "application/json"
    }
}