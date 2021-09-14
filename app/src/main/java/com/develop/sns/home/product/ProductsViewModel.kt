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
    val api = Api.initRetrofit()

    fun getProducts(token: String): LiveData<JSONObject> {
        val call = api.getCategoryList("Bearer $token")
        return apiRepository.callApi(call)
    }

    fun getProductFromCategory(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getProductByCategory("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun getProductFromVariety(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getProductByVarities("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun getProductBrands(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getProductBrands("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

}