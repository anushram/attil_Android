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

    fun initPayment(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.initPayment("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun deliverNotification(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.deliverNotification("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}