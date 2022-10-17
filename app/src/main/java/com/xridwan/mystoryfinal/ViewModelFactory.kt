package com.xridwan.mystoryfinal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xridwan.mystoryfinal.data.StoryRepository
import com.xridwan.mystoryfinal.di.Injection
import com.xridwan.mystoryfinal.ui.add.AddViewModel
import com.xridwan.mystoryfinal.ui.authentication.AuthViewModel
import com.xridwan.mystoryfinal.ui.main.MainViewModel
import com.xridwan.mystoryfinal.ui.maps.StoryMapsViewModel

class ViewModelFactory private constructor(
    private val repository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StoryMapsViewModel::class.java) -> {
                StoryMapsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also {
                instance = it
            }
    }
}