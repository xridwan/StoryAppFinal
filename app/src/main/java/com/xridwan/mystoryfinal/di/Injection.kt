package com.xridwan.mystoryfinal.di

import android.content.Context
import com.xridwan.mystoryfinal.data.StoryRepository
import com.xridwan.mystoryfinal.data.local.StoryDatabase
import com.xridwan.mystoryfinal.data.network.ApiConfig

// cek
object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}