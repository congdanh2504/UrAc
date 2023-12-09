package com.research.urac.ui.image_info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.research.urac.data.remote.model.PredictResponse
import com.research.urac.data.remote.repository.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ImageInfoViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
): ViewModel() {

    private val _status = MutableStateFlow<PredictStatus>(PredictStatus.Init)
    val status = _status.asStateFlow()

    fun predict(file: MultipartBody.Part) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _status.emit(PredictStatus.Loading)
            val predictResponse = remoteRepository.getPrediction(file)
            _status.emit(PredictStatus.Success(predictResponse))
        } catch (e: Exception) {
            Log.d("AAA", e.message.toString())
            _status.emit(PredictStatus.Error(e.message.toString()))
        }
    }

}

sealed interface PredictStatus {
    data object Init: PredictStatus
    data object Loading: PredictStatus
    data class Error(val message: String): PredictStatus
    data class Success(val predictResponse: PredictResponse): PredictStatus
}