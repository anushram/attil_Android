package com.illagu.attil.home.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class ItemDetailsViewModel(app:Application) : AndroidViewModel(app) {

    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

    fun addToCart(requestObject: JsonObject, token: String?): LiveData<JSONObject> {
        val call = api.addToCart("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}