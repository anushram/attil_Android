package com.develop.sns.home.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class ProductsViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()

    fun getTopOffers(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        lateinit var mutableLiveData: MutableLiveData<JSONObject>
        try {
            val api = Api.initRetrofit()
            val call = api.topOffersCall("Bearer $token", requestObject)
            mutableLiveData = apiRepository.callApi(call)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveData
    }

}