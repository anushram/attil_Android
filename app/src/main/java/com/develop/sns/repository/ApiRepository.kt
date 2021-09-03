package com.develop.sns.repository

import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class ApiRepository {
    private val TAG = javaClass.simpleName

    fun callApi(call: Call<ResponseBody>): MutableLiveData<JSONObject> {
        val mutableLiveData = MutableLiveData<JSONObject>()
        try {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val obj = JSONObject(response.body()?.string()!!)
                        mutableLiveData.value = obj
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveData
    }

    /*fun uploadAttachment(file: File): MutableLiveData<NetworkResponse> {
        val mutableLiveData = MutableLiveData<NetworkResponse>()
        val url = com.develop.sns.BuildConfig.BASE_API_URL + "icr/service_attachment"
        val attachmentObjRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, url, Response.Listener { response ->
                println(response)
                mutableLiveData.value = response
            }, Response.ErrorListener { }) {
            override fun getParams(): Map<String, String>? {
                //params.put("__jwToken", token);
                return HashMap()
            }

            // file name could found file base or direct access from real path
            override val byteData: Map<String, DataPart>
                protected get() {
                    val params: MutableMap<String, DataPart> = HashMap()
                    // file name could found file base or direct access from real path
                    params["attachment"] =
                        DataPart(file.name, getFileDataFromFilePath(file.absolutePath)!!)
                    return params
                }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                return params
            }
        }
        attachmentObjRequest.setShouldCache(false)
        attachmentObjRequest.retryPolicy =
            DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, backoffMultiplier)
        ApplicationManager.instance.addToRequestQueue(attachmentObjRequest, TAG)
        return mutableLiveData
    }*/
}