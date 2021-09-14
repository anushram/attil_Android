package com.develop.sns.home.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class ItemDetailsViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun addToCart(requestObject: JsonObject, token: String?): LiveData<JSONObject> {
        val call = api.addToCart("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}