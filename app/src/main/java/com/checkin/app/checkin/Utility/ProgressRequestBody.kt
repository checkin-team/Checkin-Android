package com.checkin.app.checkin.Utility

import com.checkin.app.checkin.BuildConfig
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

class ProgressRequestBody(private val mDelegate: RequestBody, private val mListener: UploadCallbacks) : RequestBody() {

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

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)

        fun onSuccess()

        fun onFailure()
    }

    internal inner class ProgressSink(delegate: Sink) : ForwardingSink(delegate) {
        private var bytesUploaded: Long = 0
        private var lastProgress = 0

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            bytesUploaded += byteCount

            if (mUpdateProgressFlag) {
                val progress = (100 * bytesUploaded / contentLength()).toInt()
                if (progress > lastProgress)
                    mListener.onProgressUpdate(progress)
                lastProgress = progress
            }

            if (bytesUploaded == contentLength())
                mUpdateProgressFlag = true
        }
    }
}