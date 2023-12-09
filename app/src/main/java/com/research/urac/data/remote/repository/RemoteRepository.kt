package com.research.urac.data.remote.repository

import com.research.urac.data.remote.datasource.PredictApi
import com.research.urac.data.remote.model.PredictResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    private val api: PredictApi
) {

    suspend fun getPrediction(file: MultipartBody.Part): PredictResponse = api.predict(file)
}