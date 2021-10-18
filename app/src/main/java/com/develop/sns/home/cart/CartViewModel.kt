package com.develop.sns.home.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class CartViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun getCartList(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getCartList("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}