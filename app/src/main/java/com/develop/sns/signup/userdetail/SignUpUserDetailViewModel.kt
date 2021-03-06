package com.develop.sns.signup.userdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class SignUpUserDetailViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun checkUserNameAvailability(requestObject: JsonObject): LiveData<JSONObject>? {
        val call = api.isUserNameExist("", requestObject)
        return apiRepository.callApi(call)
    }
}