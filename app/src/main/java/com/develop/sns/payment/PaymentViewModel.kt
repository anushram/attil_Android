package com.develop.sns.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class PaymentViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun getSavedAddressList(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getSavedAddrList("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun saveAddress(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.saveAddress("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun findShop(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.findShop("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}