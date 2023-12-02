package com.illagu.attil.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class CartViewModel(app:Application) : AndroidViewModel(app) {

    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

    fun getCartItem(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getCartItems("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun removeCartItem(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.removeCartItem("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}