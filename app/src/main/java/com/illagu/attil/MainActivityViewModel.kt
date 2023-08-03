package com.illagu.attil

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.Api
import com.illagu.attil.repository.ApiRepository
import org.json.JSONObject

class MainActivityViewModel(app:Application) : AndroidViewModel(app) {
    private val apiRepository: ApiRepository = ApiRepository()
    private val context = getApplication<Application>().applicationContext
    private val api = Api.initRetrofit(context)

    fun getSystemConfig(): LiveData<JSONObject> {
        val call = api.getSystemConfig()
        return apiRepository.callApi(call)
    }

    fun getProductList(token: String): LiveData<JSONObject> {
        val call = api.getCategoryList("Bearer $token")
        return apiRepository.callApi(call)
    }

}