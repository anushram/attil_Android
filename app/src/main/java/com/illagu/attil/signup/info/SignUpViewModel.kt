package com.illagu.attil.signup.info

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class SignUpViewModel(app:Application) : AndroidViewModel(app) {
    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

    fun createAccount(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.createAccount("", requestObject)
        return apiRepository.callApi(call)
    }
}