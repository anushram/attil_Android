package com.develop.sns.home.offers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.ApiRepository
import org.json.JSONObject

class OffersViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private var mutableLiveData: MutableLiveData<JSONObject>? = null
    private var normalMutableLiveData: MutableLiveData<JSONObject>? = null
    private var countMutableLiveData: MutableLiveData<JSONObject>? = null

    fun getTopOffers(
        url: String?,
        restType: Int,
        requestObject: JSONObject?,
        token: String?
    ): LiveData<JSONObject>? {
        if (mutableLiveData == null) {
            mutableLiveData = apiRepository.serviceCall(url, restType, requestObject, token)
        }
        return mutableLiveData
    }

    fun getNormalOffers(
        url: String?,
        restType: Int,
        requestObject: JSONObject?,
        token: String?
    ): LiveData<JSONObject>? {
        if (normalMutableLiveData == null) {
            normalMutableLiveData = apiRepository.serviceCall(url, restType, requestObject, token)
        }
        return normalMutableLiveData
    }

    fun getCartCount(
        url: String?,
        restType: Int,
        requestObject: JSONObject?,
        token: String?
    ): LiveData<JSONObject>? {
        if (countMutableLiveData == null) {
            countMutableLiveData = apiRepository.serviceCall(url, restType, requestObject, token)
        }
        return countMutableLiveData
    }
}