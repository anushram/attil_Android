package com.develop.sns.repository

import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class ApiRepository {

    fun callApi(call: Call<ResponseBody>): MutableLiveData<JSONObject> {
        val mutableLiveData = MutableLiveData<JSONObject>()
        try {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val obj = JSONObject(response.body()?.string()!!)
                        mutableLiveData.value = obj
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveData
    }
}