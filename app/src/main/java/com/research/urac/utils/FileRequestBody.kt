package com.research.urac.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Source
import okio.source
import java.io.InputStream

class FileRequestBody(
    private val inputStream: InputStream,
    private val type: String
) :
    RequestBody() {

    override fun contentType(): MediaType? {
        return "$type/*".toMediaTypeOrNull()
    }

    override fun writeTo(sink: BufferedSink) {
        var source: Source? = null
        try {
            source = inputStream.source()
            sink.writeAll(source)
        } catch (e: Exception) {
            source?.close()
        }
    }
}