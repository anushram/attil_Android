package com.illagu.attil.login.otp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.illagu.attil.MainActivityViewModel
import com.illagu.attil.R
import com.illagu.attil.SubModuleActivity
import com.illagu.attil.customviews.otpview.OnOtpCompletionListener
import com.illagu.attil.databinding.ActivityOtpBinding
import com.illagu.attil.home.HomeActivity
import com.illagu.attil.login.LoginActivity
import com.illagu.attil.signup.dto.SignUpDto
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.AppUtils
import com.illagu.attil.utils.CommonClass
import com.google.gson.JsonObject
import com.illagu.attil.signup.password.SignUpPasswordActivity
import org.json.JSONObject
import java.util.*


class OtpActivity : SubModuleActivity() {
    private val context: Context = this@OtpActivity
    private val binding by lazy { ActivityOtpBinding.inflate(layoutInflater) }

    private var submitFlag = false
    private var mobileNo: String? = null
    private var otp: String? = null
    private var isSignUp: Boolean? = null
    var signUpDto: SignUpDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        getIntentValue()
        initToolBar()
        handleUiElement()

    }

    private fun getIntentValue() {
        try {
            val intent = intent
            mobileNo = intent.getStringExtra("mobileNo")
            otp = intent.getStringExtra("otp")
            isSignUp = intent.getBooleanExtra("isSignUp", false)
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
            binding.lnToolbar.toolbar.title = resources.getString(R.string.verify_otp)
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
                        resources.getString(R.string.invalid_otp),
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
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "_id",
                        preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_OTP_ID)
                    )
                    requestObject.addProperty("isOtpVerified", true)
                    requestObject.addProperty("preferredLanguage", language)
                    //Log.e("Request", requestObject.toString())
                    showProgressBar()
                    val otpViewModel = OtpViewModel(application)
                    if (isSignUp == true) {
                        otpViewModel.verifyOtpService(
                            requestObject
                        ).observe(this, { currencyPojos ->
                            //Log.e("currencyPojos", currencyPojos.toString() + "")
                            if (currencyPojos != null) {
                                dismissProgressBar()
                                parseVerifyOtpResponse(currencyPojos)
                            }
                        })
                    } else {
                        otpViewModel.verifyLoginOtpService(
                            requestObject
                        ).observe(this, { currencyPojos ->
                            //Log.e("currencyPojos", currencyPojos.toString() + "")
                            if (currencyPojos != null) {
                                //dismissProgressBar()
                                parseVerifyOtpResponse(currencyPojos)
                            }
                        })
                    }
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

    private fun parseVerifyOtpResponse(obj: JSONObject) {
        try {
            submitFlag = false
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
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

                        if (dataObject.has("phoneVerficationId") && !dataObject.isNull("phoneVerficationId")) {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_OTP_ID,
                                dataObject.getString("phoneVerficationId")
                            )
                            if (isSignUp == true) {
                                signUpDto?.id = dataObject.getString("phoneVerficationId")
                                signUpDto?.mobileNo = mobileNo
                                preferenceHelper!!.saveValueToSharedPrefs(
                                    AppConstant.KEY_MOBILE_NO,
                                    mobileNo
                                )
                            }
                        }

                        if (dataObject.has("phoneNumber") && !dataObject.isNull("phoneNumber")) {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_MOBILE_NO,
                                dataObject.getString("phoneNumber")
                            )
                        }

                        if (isSignUp == false) {
                            val mainActivityModel = MainActivityViewModel(application)
                            mainActivityModel.getProductList(
                                preferenceHelper!!.getValueFromSharedPrefs(
                                    AppConstant.KEY_TOKEN
                                )!!
                            )?.observeForever {
                                dismissProgressBar()
                                parseProductList(it)
                            }
                        } else {
                          //  handleResponse()
                            launchPasswordActivity()
                        }
                    }
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    )
                }
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

    private fun parseProductList(jsonObject: JSONObject?) {
        try {
            //Log.e("ProducrList", jsonObject.toString())
            if (jsonObject != null) {
                if (jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_PRODUCTS_OBJ,
                        jsonObject.toString()
                    )

                    if (LoginActivity().fa != null) {
                        LoginActivity().fa!!.finish()
                    }
                    launchHomeActivity()
                } else {
                    CommonClass.handleErrorResponse(context, jsonObject, binding.rootView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchHomeActivity() {
        try {
            finishAffinity()
            val intent = Intent(this@OtpActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchPasswordActivity() {
        val intent = Intent(this, SignUpPasswordActivity::class.java)
        intent.putExtra("mobileNo",mobileNo)
        intent.putExtra("phoneVerficationId",preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_OTP_ID))
       // intent.putExtra("otp", otp)
        //intent.putExtra("isSignUp", true)
        intent.putExtra("signUpDto", signUpDto)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        resultLauncher.launch(intent)
        //startActivity(intent)
        //finish()
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
}