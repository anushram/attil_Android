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
        val call = api.verifyOtp("", requestObject)
        return apiRepository.callApi(call)
    }

    fun verifyLoginOtpService(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.otpLoginVerify("", requestObject)
        return apiRepository.callApi(call)
    }
}