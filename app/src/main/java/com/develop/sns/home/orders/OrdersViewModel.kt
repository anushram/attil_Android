package com.develop.sns.home.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class OrdersViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun getOnGoingOrders(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getOnGoingOrders("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}