package com.illagu.attil.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.illagu.attil.repository.ApiRepository
import org.json.JSONObject

class HomeViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private var mutableLiveData: MutableLiveData<JSONObject>? = null
}