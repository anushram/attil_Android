package com.illagu.attil.address

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class AddressViewModel(app:Application) : AndroidViewModel(app) {

    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

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