package com.develop.sns.login.otp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.customviews.otpview.OnOtpCompletionListener
import com.develop.sns.databinding.ActivityOtpBinding
import com.develop.sns.networkhandler.AppUrlManager
import com.develop.sns.signup.dto.SignUpDto
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import org.json.JSONObject
import java.util.*


class OtpActivity : SubModuleActivity() {
    private val context: Context = this@OtpActivity
    private val binding by lazy { ActivityOtpBinding.inflate(layoutInflater) }

    private var submitFlag = false
    private var otpViewModel: OtpViewModel? = null
    private var mobileNo: String? = null
    private var otp: String? = null
    private var isSignUp: Boolean? = null
    var signUpDto: SignUpDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        getIntentValue()
        initToolBar()
        handleUiElement()

    }

    private fun getIntentValue() {
        try {
            otpViewModel = OtpViewModel()
            val intent = intent
            mobileNo = intent.getStringExtra("mobileNo")
            otp = intent.getStringExtra("otp")
            isSignUp = intent.getBooleanExtra("isSignUp", false);
            if (intent.hasExtra("signUpDto")) {
                signUpDto = intent.getSerializableExtra("signUpDto") as SignUpDto?
            }
            binding.etOtp.requestFocus()
            showKeyboard()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showKeyboard() {
        try {
            val inputMethodManager: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInputFromWindow(
                currentFocus!!.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun initToolBar() {
        try {
            (binding.lnToolbar.toolbar as Toolbar).setTitle(getResources().getString(R.string.otp_verification))
            setSupportActionBar(binding.lnToolbar.toolbar as Toolbar)
            assert(getSupportActionBar() != null)
            getSupportActionBar()?.setDisplayShowHomeEnabled(true)
            (binding.lnToolbar.toolbar as Toolbar).setNavigationIcon(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_action_back
                )
            )
            (binding.lnToolbar.toolbar as Toolbar).layoutDirection =
                View.LAYOUT_DIRECTION_LTR
            (binding.lnToolbar.toolbar as Toolbar).setNavigationOnClickListener { onBackPressed() }
        } catch (bug: Exception) {
            bug.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.etOtp.setOtpCompletionListener(OnOtpCompletionListener {
                hideKeyboard()
                if (otp.equals(binding.etOtp.text.toString(), false)) {
                    verifyOtpService()
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rootView,
                        getResources().getString(R.string.invalid_otp),
                        Toast.LENGTH_SHORT
                    )
                }
            })

            binding.lnError.ivClose.setOnClickListener(View.OnClickListener { hideErrorMessage() })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //VerifyOtp service
    private fun verifyOtpService() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                try {
                    val requestObject = JSONObject()
                    requestObject.put(
                        "_id",
                        preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_OTP_ID)
                    )
                    requestObject.put("isOtpVerified", true)
                    requestObject.put("preferredLanguage", language)
                    Log.e("Request", requestObject.toString())
                    showProgressBar()
                    var url: String = ""
                    if (isSignUp == true) {
                        url = AppUrlManager.getAPIUrl().toString() + "otp/verify"
                    } else {
                        url = AppUrlManager.getAPIUrl().toString() + "auth/otpLoginVerify"
                    }

                    otpViewModel!!.verifyOtpService(
                        url,
                        AppConstant.REST_CALL_POST,
                        requestObject
                    )?.observe(this, { currencyPojos ->
                        Log.e("currencyPojos", currencyPojos.toString() + "")
                        if (currencyPojos != null) {
                            dismissProgressBar()
                            parseVerifyOtpResponse(currencyPojos)
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                CommonClass.showToastMessage(
                    context,
                    binding.rootView,
                    getResources().getString(R.string.no_internet),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseVerifyOtpResponse(obj: JSONObject) {
        try {
            submitFlag = false
            if (obj.has("data") && !obj.isNull("data")) {
                val dataObject = obj.getJSONObject("data")

                if (dataObject.has("access_token") && !dataObject.isNull("access_token")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_TOKEN,
                        dataObject.getString("access_token")
                    )
                }

                if (dataObject.has("userId") && !dataObject.isNull("userId")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_USER_ID,
                        dataObject.getString("userId")
                    )
                }

                if (dataObject.has("username") && !dataObject.isNull("username")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_NAME,
                        dataObject.getString("username")
                    )
                }

                if (dataObject.has("_id") && !dataObject.isNull("_id")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_OTP_ID,
                        dataObject.getString("_id")
                    )
                    if (isSignUp == true) {
                        signUpDto?.id = dataObject.getString("_id")
                    }
                }

                if (dataObject.has("phoneNumber") && !dataObject.isNull("phoneNumber")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_MOBILE_NO,
                        dataObject.getString("phoneNumber")
                    )
                    if (isSignUp == true) {
                        signUpDto?.mobileNo = dataObject.getString("phoneNumber")
                    }
                }
                handleResponse()
            } else {
                CommonClass.showToastMessage(
                    context,
                    binding.rootView,
                    obj.getString("message"),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleResponse() {
        try {
            val intent = Intent()
            if (isSignUp == true) {
                intent.putExtra("signUpDto", signUpDto)
            }
            setResult(Activity.RESULT_OK, intent)
            this@OtpActivity.finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = OtpActivity::class.java.simpleName
    }

    open fun showErrorMessage(errorMessage: String?) {
        try {
            binding.lnError.tvMessage!!.setText(errorMessage)
            binding.lnError.lnErrorMain.visibility = View.VISIBLE
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    open fun hideErrorMessage() {
        try {
            binding.lnError.lnErrorMain.visibility = View.GONE
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}