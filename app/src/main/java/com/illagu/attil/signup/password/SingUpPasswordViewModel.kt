package com.illagu.attil.signup.password

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import org.json.JSONObject

class SingUpPasswordViewModel(app:Application) : AndroidViewModel(app) {

    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

    fun createAccountService(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.createAccount("", requestObject)
        return apiRepository.callApi(call)
    }
}