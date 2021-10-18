package com.develop.sns.home.cart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityCartListBinding
import com.develop.sns.login.otp.OtpViewModel
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.gson.JsonObject
import org.json.JSONObject

class CartListActivity : SubModuleActivity() {
    private val context: Context = this@CartListActivity
    private val binding by lazy { ActivityCartListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initToolBar()
        getIntentValue()
        getCartList()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title = resources.getString(R.string.otp_verification)
            setSupportActionBar(binding.lnToolbar.toolbar)
            assert(supportActionBar != null)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            binding.lnToolbar.toolbar.navigationIcon =
                ContextCompat.getDrawable(context, R.drawable.ic_action_back)
            binding.lnToolbar.toolbar.layoutDirection = View.LAYOUT_DIRECTION_LTR
            binding.lnToolbar.toolbar.setNavigationOnClickListener { onBackPressed() }
        } catch (bug: Exception) {
            bug.printStackTrace()
        }
    }

    private fun getIntentValue() {
        try {
            val intent = intent
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getCartList() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                try {
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "userId",
                        preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                    )
                    Log.e("Request", requestObject.toString())
                    showProgressBar()
                    val cartViewModel = CartViewModel()
                    cartViewModel.getCartList(
                        requestObject,
                        preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                    ).observe(this, { currencyPojos ->
                        Log.e("currencyPojos", currencyPojos.toString() + "")
                        if (currencyPojos != null) {
                            dismissProgressBar()
                            parseCartListResponse(currencyPojos)
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                CommonClass.showToastMessage(
                    context,
                    binding.rootView,
                    resources.getString(R.string.no_internet),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseCartListResponse(jsonObject: JSONObject) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}