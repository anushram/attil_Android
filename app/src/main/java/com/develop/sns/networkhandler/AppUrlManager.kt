package com.develop.sns.networkhandler

import com.develop.sns.utils.AppConstant


public class AppUrlManager {
    companion object {
        /**
         * Note: When changing the apiUrlVal and apiVersionVal, change in NetworkHandler file aslo for ACRA.
         * uncomment the deviceId in sign up pages
         */
        private val apiUrlVal: Int =
            AppConstant.URL_TEST // For LIVE-- URL_LIVE
        private val versionUrlVal: Int =
            AppConstant.URL_VERSION_CODE_A // Switch to URL_VERSION_CODE_B for live
        private val apiVersionVal: String =
            AppConstant.LIVE_API_VERSION_B // Change it to LIVE_API_VERSION_B for live//LIVE//UAT

        fun getAPIUrl(): String? {
            var apiUrl = ""
            try {
                if (apiUrlVal == AppConstant.URL_TEST) {
                    apiUrl = "http://yaazhproducts.herokuapp.com/" //TEST
                } else if (apiUrlVal == AppConstant.URL_UAT) {
                    apiUrl = "" //UAT
                } else if (apiUrlVal == AppConstant.URL_LIVE) {
                    apiUrl = "" //LIVE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return apiUrl
        }

        fun getVersionAPIUrl(): String? {
            var apiUrl = ""
            try {
                if (versionUrlVal == AppConstant.URL_VERSION_CODE_A) {
                    apiUrl = "version/A" //TEST
                } else if (versionUrlVal == AppConstant.URL_VERSION_CODE_B) {
                    apiUrl = "version/B" //UAT
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return apiUrl
        }
    }
}