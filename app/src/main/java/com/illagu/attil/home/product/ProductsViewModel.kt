package com.illagu.attil.home.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class ProductsViewModel(app:Application) : AndroidViewModel(app) {
    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    val api = Api.initRetrofit(context)

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