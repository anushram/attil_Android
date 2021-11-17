package com.develop.sns.signup.mobileno

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivitySignUpMobileBinding
import com.develop.sns.login.otp.OtpActivity
import com.develop.sns.signup.dto.SignUpDto
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.gson.JsonObject
import org.json.JSONObject


class SignUpMobileActivity : SubModuleActivity() {

    private val context: Context = this@SignUpMobileActivity
    private val binding by lazy { ActivitySignUpMobileBinding.inflate(layoutInflater) }

    private var submitFlag = false
    var otp = ""
    var signUpDto: SignUpDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        initToolBar()
        getIntentValue()
        initClassReference()
        handleUiElement()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title = resources.getString(R.string.verify)
            setSupportActionBar(binding.lnToolbar.toolbar)
            assert(supportActionBar != null)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            binding.lnToolbar.toolbar.navigationIcon = ContextCompat.getDrawable(
                context,
                R.drawable.ic_action_back
            )
            binding.lnToolbar.toolbar.layoutDirection =
                View.LAYOUT_DIRECTION_LTR
            binding.lnToolbar.toolbar.setNavigationOnClickListener { onBackPressed() }
        } catch (bug: Exception) {
            bug.printStackTrace()
        }
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            signUpDto = intent.getSerializableExtra("signUpDto") as SignUpDto?
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnError.ivClose.setOnClickListener(View.OnClickListener { hideErrorMessage() })

            binding.btnVerify.setOnClickListener(View.OnClickListener {
                if (submitFlag == false) {
                    submitFlag = true
                    sendOtpService()
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateMob(): Boolean {
        var flag = true
        try {
            if (binding.etMobileNo.text.toString().isEmpty()) {
                binding.etMobileNo.requestFocus()
                binding.etMobileNo.error = resources.getString(R.string.required)
                flag = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        submitFlag = flag
        return flag
    }

    private fun sendOtpService() {
        try {
            if (validateMob()) {
                if (AppUtils.isConnectedToInternet(context)) {
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "deviceToken",
                        preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_GCM_ID)!!
                    )
                    requestObject.addProperty("phoneNumber", binding.etMobileNo.text.toString())
                    requestObject.addProperty("role", "user")
                    requestObject.addProperty("preferredLanguage", language)

                    showProgressBar()
                    val signUpMobileViewModel = SignUpMobileViewModel()
                    signUpMobileViewModel.sendOtpService(
                        requestObject
                    ).observe(this, Observer<JSONObject?> { currencyPojos ->
                        if (currencyPojos != null) {
                            dismissProgressBar()
                            parseGenerateOtpResponse(currencyPojos)
                        }
                    })
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rootView,
                        resources.getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseGenerateOtpResponse(obj: JSONObject) {
        try {
            submitFlag = false
//            if (obj.has("code") && obj.getInt("code") == 200) {
//                if (obj.has("status") && obj.getBoolean("status")) {
            if (obj.has("data") && !obj.isNull("data")) {
                val dataObject = obj.getJSONObject("data")
                if (dataObject.has("_id") && !dataObject.isNull("_id")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_OTP_ID,
                        dataObject.getString("_id")
                    )
                }

                if (dataObject.has("otp") && !dataObject.isNull("otp")) {
                    otp = dataObject.getString("otp")
                    Log.e("otp", otp)
                }
                launchOTPVerifyActivity()
            } /*}*/ else {
                CommonClass.showToastMessage(
                    context,
                    binding.rootView,
                    obj.getString("message"),
                    Toast.LENGTH_SHORT
                )
            }
            /*} else {
                CommonClass.showToastMessage(
                    context,
                    binding.rootView,
                    obj.getString("message"),
                    Toast.LENGTH_SHORT
                );
            }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                signUpDto = data?.getSerializableExtra("signUpDto") as SignUpDto?
                handleResponse()
            }
        }

    private fun launchOTPVerifyActivity() {
        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra("mobileNo", binding.etMobileNo.text.toString())
        intent.putExtra("otp", otp)
        intent.putExtra("isSignUp", true)
        intent.putExtra("signUpDto", signUpDto)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        resultLauncher.launch(intent)
    }

    private fun handleResponse() {
        try {
            val intent = Intent()
            intent.putExtra("signUpDto", signUpDto)
            setResult(RESULT_OK, intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}