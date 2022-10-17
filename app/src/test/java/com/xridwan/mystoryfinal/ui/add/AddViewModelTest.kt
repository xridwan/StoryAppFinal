package com.xridwan.mystoryfinal.ui.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.xridwan.mystoryfinal.DataDummy
import com.xridwan.mystoryfinal.data.Resource
import com.xridwan.mystoryfinal.data.StoryRepository
import com.xridwan.mystoryfinal.getOrAwaitValue
import com.xridwan.mystoryfinal.data.network.response.FileUploadResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addViewModel: AddViewModel
    private val dummyAddStory = DataDummy.dummyAddStoryResponse()

    @Before
    fun setUp() {
        addViewModel = AddViewModel(storyRepository)
    }

    @Test
    fun postImage() {
        val description = "This is Description".toRequestBody("text/plain".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "nameFile",
            requestImageFile
        )

        val expectedStory = MutableLiveData<Resource<FileUploadResponse>>()
        expectedStory.value = Resource.Success(dummyAddStory)
        Mockito.`when`(
            storyRepository.postImage(
                imageMultipart, description, LAT, LON, TOKEN,
                ACCEPT
            )
        ).thenReturn(expectedStory)

        val actualStory = addViewModel.postImage(
            imageMultipart, description, LAT, LON, TOKEN,
            ACCEPT
        ).getOrAwaitValue()

        Mockito.verify(storyRepository).postImage(
            imageMultipart, description, LAT, LON, TOKEN,
            ACCEPT
        )
        assertNotNull(actualStory)
        assertTrue(actualStory is Resource.Success)
    }

    companion object {
        private const val TOKEN = "Bearer TOKEN"
        private const val LAT = 1.00
        private const val LON = 1.00
        private const val ACCEPT = "application/json"
    }
}