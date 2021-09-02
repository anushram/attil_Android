package com.develop.sns.home.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.ApiRepository
import org.json.JSONObject

class ItemDetailsViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private var mutableLiveData: MutableLiveData<JSONObject>? = null

    fun addToCart(
        url: String?,
        restType: Int,
        encryptedObjectBeforeToken: JSONObject?,
        token: String?
    ): LiveData<JSONObject>? {
        if (mutableLiveData == null) {
            mutableLiveData =
                apiRepository.serviceCall(url, restType, encryptedObjectBeforeToken, token)
        }
        return mutableLiveData
    }
}