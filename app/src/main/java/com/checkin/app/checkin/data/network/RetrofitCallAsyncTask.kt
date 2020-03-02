package com.checkin.app.checkin.data.network

import android.os.AsyncTask
import com.checkin.app.checkin.utility.ProgressRequestBody.UploadCallbacks
import retrofit2.Call
import java.io.IOException

class RetrofitCallAsyncTask<T>(private val mListener: UploadCallbacks<T>?) : AsyncTask<Call<T>, Void?, Void?>() {
    @SafeVarargs
    override fun doInBackground(vararg calls: Call<T>): Void? {
        for (call in calls) {
            try {
                val response = call.execute()
                if (response.isSuccessful) mListener?.onSuccess(ApiResponse(response))
                else mListener?.onFailure(ApiResponse(response))
            } catch (e: IOException) {
                e.printStackTrace()
                mListener?.onFailure(ApiResponse(e))
            }
        }
        return null
    }
}