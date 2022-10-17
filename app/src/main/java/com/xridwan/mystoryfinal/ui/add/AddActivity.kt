package com.xridwan.mystoryfinal.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.xridwan.mystoryfinal.R
import com.xridwan.mystoryfinal.ViewModelFactory
import com.xridwan.mystoryfinal.data.Resource
import com.xridwan.mystoryfinal.databinding.ActivityAddBinding
import com.xridwan.mystoryfinal.preferences.UserPreferences
import com.xridwan.mystoryfinal.ui.camera.CameraActivity
import com.xridwan.mystoryfinal.ui.main.MainActivity
import com.xridwan.mystoryfinal.ui.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var preferences: UserPreferences
    private lateinit var factory: ViewModelFactory
    private val viewModel: AddViewModel by viewModels {
        factory
    }
    private var getFile: File? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        if (!permissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener {
            startCameraX()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnUpload.setOnClickListener {
            getMyLastLocation()
        }
    }

    private fun permissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
        preferences = UserPreferences(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun postImage(token: String?, location: Location) {
        if (getFile != null) {

            val file = reduceFileImage(getFile as File)
            val userToken = "Bearer $token"

            val description =
                binding.etDescription.text.toString()
                    .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val lat = location.latitude
            val lon = location.longitude

            val multiForm = "application/json"

            viewModel.postImage(
                imageMultipart,
                description,
                lat,
                lon,
                userToken,
                multiForm
            ).observe(this) { response ->
                when (response) {
                    is Resource.Loading -> {
                        binding.progressCircular.show()
                    }
                    is Resource.Success -> {
                        binding.progressCircular.hide()
                        showToast(response.data.message)
                        sendIntent()
                    }
                    is Resource.Error -> {
                        binding.progressCircular.show()
                        showToast(response.error)
                    }
                }
            }
        } else {
            showToast("Please get your image")
            binding.progressCircular.hide()
        }
    }

    private fun sendIntent() {
        startActivity(Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
        })
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            val mFile = uriToFile(selectedImage, this)
            getFile = mFile
            binding.ivPreview.setImageURI(selectedImage)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val mFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = mFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.ivPreview.setImageBitmap(result)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!permissionGranted()) {
                Toast.makeText(this, "Can't Get Permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                    Snackbar.make(
                        binding.root,
                        getString(R.string.location_permission_denied),
                        Snackbar.LENGTH_SHORT
                    )
                        .setActionTextColor(getColor(R.color.white))
                        .setAction(getString(R.string.location_permission_denied_action)) {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }.show()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val token = preferences.getToken()
                    if (!token.isNullOrEmpty()) {
                        postImage(token, location)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 100
    }
}