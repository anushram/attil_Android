package com.develop.sns.repository

import com.develop.sns.BuildConfig
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface Api {

    @POST("topOffer/allProducts")
    fun topOffersCall(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("product/getAllNormalOffers")
    fun normalOffersCall(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("customer/cartCount")
    fun cartCount(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("customer/addtoCart")
    fun addToCart(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("otp/verify")
    fun verifyOtp(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("auth/otpLoginVerify")
    fun otpLoginVerify(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("auth/otpLogin")
    fun sendOtpLogin(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("auth/login")
    fun loginService(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("createProfile")
    fun createProfile(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("otp/generate")
    fun generateOtp(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("isUserNameExist")
    fun isUserNameExist(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("getSystemConfig")
    fun getSystemConfig(): Call<ResponseBody>

    @POST("productCategory/list/all")
    fun getCategoryList(@Header("Authorization") authorization: String): Call<ResponseBody>

    @POST("product/search/list/all")
    fun getProductByCategory(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("product/search/varities/all")
    fun getProductByVarities(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("product/search/list/brands")
    fun getProductBrands(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    companion object {

        fun initRetrofit(): Api {
            val api: Api
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
            //logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.SECONDS)
                .readTimeout(60000, TimeUnit.SECONDS)
            httpClient.addInterceptor(logging)

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
            api = retrofit.create(Api::class.java)
            return api
        }
    }
}