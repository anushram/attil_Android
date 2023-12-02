package com.illagu.attil.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.HTTP
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
                    } else {
                        if (response.code() > 500) {
                            val errorObj = JSONObject()
                            errorObj.put("code", response.code())
                            errorObj.put("status", false)
                            errorObj.put("message", "Server Error")
                            mutableLiveData.value = errorObj
                        } else if (response.code() == 404) {
                            val errorObj = JSONObject()
                            errorObj.put("code", response.code())
                            errorObj.put("status", false)
                            errorObj.put("message", "Api Not Found")
                            mutableLiveData.value = errorObj
                        } else {
                            val gson = Gson()
                            val errorMessageDto = gson.fromJson(
                                response.errorBody()!!.charStream(),
                                MyErrorMessage::class.java
                            )
                            val errorObj = JSONObject()
                            errorObj.put("code", response.code())
                            errorObj.put("status", false)
                            errorObj.put("message", errorMessageDto.message)
                            mutableLiveData.value = errorObj
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val message: String = t.message.toString()
                    val errorObj = JSONObject()
                    errorObj.put("code", 0)
                    errorObj.put("status", false)
                    errorObj.put("message", message)
                    mutableLiveData.value = errorObj
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveData
    }
}