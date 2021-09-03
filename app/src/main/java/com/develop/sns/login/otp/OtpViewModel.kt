package com.develop.sns.login.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class OtpViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun verifyOtpService(requestObject: JsonObject): LiveData<JSONObject> {
        lateinit var mutableLiveData: MutableLiveData<JSONObject>
        try {
            val call = api.verifyOtp("", requestObject)
            mutableLiveData = apiRepository.callApi(call)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveData
    }

    fun verifyLoginOtpService(requestObject: JsonObject): LiveData<JSONObject> {
        lateinit var mutableLiveData: MutableLiveData<JSONObject>
        try {
            val call = api.otpLoginVerify("", requestObject)
            mutableLiveData = apiRepository.callApi(call)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveData
    }
}