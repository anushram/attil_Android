package com.develop.sns.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject


class AddressViewModel : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun getSavedAddressList(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getSavedAddrList("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}