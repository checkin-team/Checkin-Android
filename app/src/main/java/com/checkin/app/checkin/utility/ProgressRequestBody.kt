package com.checkin.app.checkin.utility

import com.checkin.app.checkin.BuildConfig
import com.checkin.app.checkin.data.network.ApiResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

class ProgressRequestBody(private val mDelegate: RequestBody, private val mListener: UploadCallbacks<*>) : RequestBody() {
    private var mUpdateProgressFlag = !BuildConfig.DEBUG

    override fun contentType(): MediaType? = mDelegate.contentType()

    override fun contentLength(): Long {
        try {
            return mDelegate.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val progressSink = ProgressSink(sink)
        val bufferedSink = Okio.buffer(progressSink)

        mDelegate.writeTo(bufferedSink)

        bufferedSink.flush()
    }

    interface UploadCallbacks<T> {
        fun onProgressUpdate(percentage: Int)

        fun onSuccess(response: ApiResponse<T>)

        fun onFailure(response: ApiResponse<T>)
    }

    internal inner class ProgressSink(delegate: Sink) : ForwardingSink(delegate) {
        private var bytesUploaded: Long = 0
        private var lastProgress = 0

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            bytesUploaded += byteCount
            val length = contentLength().takeIf { it > 0 } ?: bytesUploaded

            if (mUpdateProgressFlag) {
                val progress = (100 * bytesUploaded / length).toInt()
                if (progress > lastProgress)
                    mListener.onProgressUpdate(progress)
                lastProgress = progress
            }

            if (bytesUploaded == length) mUpdateProgressFlag = true
        }
    }
}