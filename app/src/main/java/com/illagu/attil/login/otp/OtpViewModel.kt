package com.illagu.attil.login.otp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class OtpViewModel(app:Application) : AndroidViewModel(app) {

    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

    fun verifyOtpService(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.verifyOtp("", requestObject)
        return apiRepository.callApi(call)
    }

    fun verifyLoginOtpService(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.otpLoginVerify("", requestObject)
        return apiRepository.callApi(call)
    }
}