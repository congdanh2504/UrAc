package com.research.urac.ui.image_info

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.research.urac.databinding.ActivityImageInfoBinding
import com.research.urac.extensions.createImageUri
import com.research.urac.utils.FileRequestBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

@AndroidEntryPoint
class ImageInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageInfoBinding
    private lateinit var imageUri: Uri
    private val viewModel: ImageInfoViewModel by viewModels()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.ivImage.setImageURI(it)
                viewModel.predict(uriToImageBody(it))
            }
        }

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        binding.ivImage.setImageURI(imageUri)
        viewModel.predict(uriToImageBody(imageUri))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImage()
        initListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun setupImage() {
        imageUri = createImageUri()
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(IMAGE_URI_KEY, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(IMAGE_URI_KEY)
        }
        uri?.let {
            binding.ivImage.setImageURI(it)
            viewModel.predict(uriToImageBody(it))
        }

        lifecycleScope.launch {
            viewModel.status.collect {
                when (it) {
                    PredictStatus.Init -> {
                        binding.loadingProgressBar.visibility = View.INVISIBLE
                        binding.tvPrediction.text = ""
                    }

                    is PredictStatus.Error -> {
                        binding.loadingProgressBar.visibility = View.INVISIBLE
                        binding.tvPrediction.text = "Error: ${it.message}"
                    }

                    is PredictStatus.Loading -> {
                        binding.tvPrediction.text = ""
                        binding.loadingProgressBar.visibility = View.VISIBLE
                    }

                    is PredictStatus.Success -> {
                        binding.loadingProgressBar.visibility = View.INVISIBLE
                        binding.tvPrediction.text = it.predictResponse.prediction
                    }
                }
            }
        }
    }

    private fun uriToImageBody(uri: Uri): MultipartBody.Part {
        val contentType = getContentType(uri)
        val requestBody = FileRequestBody(
            contentResolver.openInputStream(uri)!!,
            contentType ?: "jpg"
        )
        return MultipartBody.Part.createFormData("file", "file", requestBody)
    }

    @SuppressLint("Range")
    private fun getContentType(uri: Uri): String? {
        val contentResolver = contentResolver
        return contentResolver.getType(uri)
    }

    private fun initListeners() = binding.apply {
        ivLibrary.setOnClickListener {
            pickMedia.launch("image/*")
        }
        ivCamera.setOnClickListener {
            Dexter.withContext(this@ImageInfoActivity).withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    p0?.let {
                        if (it.areAllPermissionsGranted()) {
                            contract.launch(imageUri)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {

                }

            }).onSameThread().check()
        }
    }

    companion object {
        const val IMAGE_URI_KEY = "image_uri_key"
    }
}