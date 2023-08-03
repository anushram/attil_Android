package com.illagu.attil.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class PaymentViewModel(app:Application) : AndroidViewModel(app) {

    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

    fun initPayment(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.initPayment("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun deliverNotification(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.deliverNotification("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}