package com.develop.sns.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.ApiRepository
import org.json.JSONObject

class HomeViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private var mutableLiveData: MutableLiveData<JSONObject>? = null
}