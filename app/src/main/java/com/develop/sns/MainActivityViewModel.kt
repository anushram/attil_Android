package com.develop.sns

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import org.json.JSONObject

class MainActivityViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun getSystemConfig(): LiveData<JSONObject>? {
        val call = api.getSystemConfig()
        return apiRepository.callApi(call)
    }

    fun getProductList(token: String): LiveData<JSONObject>? {
        val call = api.getCategoryList("Bearer $token")
        return apiRepository.callApi(call)
    }

}