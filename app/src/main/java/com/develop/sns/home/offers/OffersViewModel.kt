package com.develop.sns.home.offers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class OffersViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun getTopOffers(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.topOffersCall("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }


    fun getNormalOffers(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.normalOffersCall("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun getCartCount(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.cartCount("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}