package com.research.urac.data.remote.datasource

import com.research.urac.data.remote.model.PredictResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PredictApi {

    @Multipart
    @POST("predict_image")
    suspend fun predict(@Part file: MultipartBody.Part): PredictResponse
}