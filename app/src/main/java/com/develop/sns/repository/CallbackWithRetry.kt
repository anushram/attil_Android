package com.develop.sns.repository

import retrofit2.Call
import retrofit2.Callback

abstract class CallbackWithRetry<T>(private val call: Call<T>) : Callback<T> {
    private var retryCount = 0

    override fun onFailure(call: Call<T>, t: Throwable) {
        //Log.e(TAG, t.localizedMessage)
        if (retryCount++ < TOTAL_RETRIES) {
            //Log.i(TAG, "Retrying... ($retryCount out of $TOTAL_RETRIES)")
            retry()
        }
    }

    private fun retry() {
        call.clone().enqueue(this)
    }

    companion object {
        private const val TOTAL_RETRIES = 5
        private val TAG = CallbackWithRetry::class.java.simpleName
    }
}