package com.develop.sns.login.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.NetworkResponse
import com.develop.sns.repository.ApiRepository
import org.json.JSONObject
import java.io.File

class OtpViewModel : ViewModel() {
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

    fun verifyOtpService(
        url: String?,
        restType: Int,
        encryptedObjectBeforeToken: JSONObject?
    ): LiveData<JSONObject>? {
        if (mutableLiveData == null) {
            mutableLiveData = apiRepository.serviceCall(url, restType, encryptedObjectBeforeToken,"")
        }
        return mutableLiveData
    }
}