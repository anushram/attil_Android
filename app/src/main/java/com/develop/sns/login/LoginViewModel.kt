package com.develop.sns.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.ApiRepository
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private var mutableLiveData: MutableLiveData<JSONObject>? = null

    fun sendOtpService(
        url: String?,
        restType: Int,
        encryptedObjectBeforeToken: JSONObject?
    ): LiveData<JSONObject>? {
        if (mutableLiveData == null) {
            mutableLiveData = apiRepository.serviceCall(url, restType, encryptedObjectBeforeToken,"")
        }
        return mutableLiveData
    }

    fun makeLogin(
        url: String?,
        restType: Int,
        jsonObject: JSONObject?
    ): LiveData<JSONObject>? {
        if (mutableLiveData == null) {
            mutableLiveData = apiRepository.serviceCall(url, restType, jsonObject,"")
        }
        return mutableLiveData
    }

}