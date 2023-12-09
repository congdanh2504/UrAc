package com.research.urac.ui.source_select

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.research.urac.databinding.ActivitySourceSelectBinding
import com.research.urac.extensions.createImageUri
import com.research.urac.ui.image_info.ImageInfoActivity
import com.research.urac.ui.image_info.ImageInfoActivity.Companion.IMAGE_URI_KEY

class SourceSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySourceSelectBinding
    private var imageUri: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { nonNullUri ->
                startActivity(
                    Intent(
                        this@SourceSelectActivity,
                        ImageInfoActivity::class.java
                    ).also {
                        it.putExtra(IMAGE_URI_KEY, nonNullUri)
                    })
            }

        }

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        imageUri?.let { nonNullUri ->
            startActivity(Intent(this@SourceSelectActivity, ImageInfoActivity::class.java).also {
                it.putExtra(IMAGE_URI_KEY, nonNullUri)
            })
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourceSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUri = createImageUri()
        initListeners()
    }

    private fun initListeners() = binding.apply {
        cvLibrary.setOnClickListener {
            pickMedia.launch("image/*")
        }
        cvCamera.setOnClickListener {
            Dexter.withContext(this@SourceSelectActivity).withPermissions(
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
}