package com.research.urac.extensions

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun AppCompatActivity.createImageUri(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val image = File(filesDir, "$timeStamp.png")
    return FileProvider.getUriForFile(
        this,
        "com.research.urac.FileProvider",
        image
    )
}