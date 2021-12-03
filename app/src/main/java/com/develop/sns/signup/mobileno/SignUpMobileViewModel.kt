package com.develop.sns.signup.mobileno

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class SignUpMobileViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun sendOtpService(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.generateOtp("", requestObject)
        return apiRepository.callApi(call)
    }
}