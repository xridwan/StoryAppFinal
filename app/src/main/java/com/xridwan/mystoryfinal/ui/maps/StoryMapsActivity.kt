package com.xridwan.mystoryfinal.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.xridwan.mystoryfinal.R
import com.xridwan.mystoryfinal.ViewModelFactory
import com.xridwan.mystoryfinal.data.Resource
import com.xridwan.mystoryfinal.data.network.response.ListStoryItem
import com.xridwan.mystoryfinal.databinding.ActivityStoryMapsBinding
import com.xridwan.mystoryfinal.preferences.UserPreferences
import com.xridwan.mystoryfinal.ui.utils.showToast

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityStoryMapsBinding
    private lateinit var preferences: UserPreferences
    private lateinit var mMap: GoogleMap
    private lateinit var factory: ViewModelFactory
    private val viewModel: StoryMapsViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        preferences = UserPreferences(this)

        getStoryMap()
    }

    private fun getStoryMap() {
        val token = "Bearer ${preferences.getToken()}"
        viewModel.getStoriesWithLocation(1, token).observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    Log.e(TAG, "getStoriesLocation: Loading..")
                }
                is Resource.Success -> {
                    showMarker(response.data.listStory)
                }
                is Resource.Error -> {
                    showToast(response.error)
                }
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showMarker(listStory: List<ListStoryItem>) {
        for (story in listStory) {
            val latLng = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .snippet(story.description)
                    .title(story.name)
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        getMyLocation()
        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    companion object {
        private val TAG = StoryMapsActivity::class.simpleName
    }
}