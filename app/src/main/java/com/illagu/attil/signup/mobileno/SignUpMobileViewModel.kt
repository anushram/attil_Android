package com.illagu.attil.signup.mobileno

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class SignUpMobileViewModel(app:Application) : AndroidViewModel(app) {

    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

    fun sendOtpService(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.generateOtp("", requestObject)
        return apiRepository.callApi(call)
    }
}