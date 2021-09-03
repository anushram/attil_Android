package com.develop.sns.signup.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class SignUpViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun createAccount(requestObject:JsonObject): LiveData<JSONObject> {
        lateinit var mutableLiveData: MutableLiveData<JSONObject>
        try {
            val call = api.createProfile("", requestObject)
            mutableLiveData = apiRepository.callApi(call)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveData
    }
}