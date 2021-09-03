package com.develop.sns

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonNull
import org.json.JSONObject

class MainActivityViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun getSystemConfig(): LiveData<JSONObject>? {
        lateinit var mutableLiveData: MutableLiveData<JSONObject>
        try {
            val call = api.getSystemConfig()
            mutableLiveData = apiRepository.callApi(call)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveData
    }

}