package com.develop.sns.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.develop.sns.MainActivityViewModel
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityLoginBinding
import com.develop.sns.home.HomeActivity
import com.develop.sns.login.otp.OtpActivity
import com.develop.sns.signup.info.SignUpInfoActivity
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import org.json.JSONObject


class LoginActivity : SubModuleActivity() {
    private val context: Context = this@LoginActivity
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override var preferenceHelper: PreferenceHelper? = null
    private var submitFlag = false
    var gcmId = ""
    var otp = ""
    lateinit var myAnim: Animation

    var fa: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fa = this

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        initClassReference()
        checkFireBaseToken()
        handleUiElement()
    }

    private fun initClassReference() {
        try {
            preferenceHelper = PreferenceHelper(context)
            languageId = preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            myAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_in_out)
            binding.btnSignUp.startAnimation(myAnim)
            binding.cbShowPassword.setButtonDrawable(R.drawable.password_show_drawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkFireBaseToken() {
        try {
            gcmId = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_GCM_ID)!!
            if (gcmId.trim { it <= ' ' }.isEmpty()) {
                gcmId = getFireBaseToken()
                preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_GCM_ID, gcmId)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getFireBaseToken(): String {
        var gcmId = ""
        try {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    gcmId = task.result!!
                    preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_GCM_ID, gcmId)
                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return gcmId
    }

    private fun handleUiElement() {
        try {
            binding.lnError.ivClose.setOnClickListener(View.OnClickListener { hideErrorMessage() })

            binding.etMobileNo.setOnEditorActionListener(OnEditorActionListener { v, actionId, event -> //////Log.i("onEditorAction", "onEditorAction");
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                    binding.lnAfterMobileNo.visibility = View.VISIBLE
                    return@OnEditorActionListener true
                }
                false
            })

            binding.btnLoginPassword.setOnClickListener(View.OnClickListener {
                binding.rlPasswordView.visibility = View.VISIBLE
                binding.btnLoginPassword.visibility = View.GONE
                binding.tvOr.visibility = View.VISIBLE
                binding.lnLoginOtp.visibility = View.VISIBLE
            })

            binding.cbShowPassword.setOnCheckedChangeListener { buttonView, isChecked ->
                val start: Int
                val end: Int
                if (!isChecked) {
                    start = binding.etPassword.selectionStart
                    end = binding.etPassword.selectionEnd
                    binding.etPassword.transformationMethod = PasswordTransformationMethod()
                    binding.etPassword.setSelection(start, end)
                } else {
                    start = binding.etPassword.selectionStart
                    end = binding.etPassword.selectionEnd
                    binding.etPassword.transformationMethod = null
                    binding.etPassword.setSelection(start, end)
                }
            }

            binding.btnLoginOtp.setOnClickListener(View.OnClickListener {
                if (submitFlag == false) {
                    submitFlag = true
                    sendOtpService()
                }
            })

            binding.btnSignIn.setOnClickListener(View.OnClickListener {
                hideErrorMessage()
                if (!submitFlag) {
                    submitFlag = true
                    logInService()
                }
            })

            binding.btnSignUp.setOnClickListener(View.OnClickListener {
                binding.btnSignUp.clearAnimation()
                launchSignUpActivity()
            })

            binding.rlLoginMainLayout.viewTreeObserver.addOnGlobalLayoutListener {
                val rec = Rect()
                binding.rlLoginMainLayout.getWindowVisibleDisplayFrame(rec)
                //finding screen height
                val screenHeight = binding.rlLoginMainLayout.rootView.height
                //finding keyboard height
                val keypadHeight = screenHeight - rec.bottom
                if (keypadHeight > screenHeight * 0.15) {

                } else {
                    if (!binding.etMobileNo.text.isEmpty() && binding.etMobileNo.text.length == 10) {
                        binding.lnAfterMobileNo.visibility = View.VISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var signUpLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val mainActivityModel = MainActivityViewModel()
                mainActivityModel.getProductList(
                    preferenceHelper!!.getValueFromSharedPrefs(
                        AppConstant.KEY_TOKEN
                    )!!
                )?.observeForever {
                    dismissProgressBar()
                    parseProductList(it)
                }
            }
        }

    private fun launchSignUpActivity() {
        try {
            val intent = Intent(context, SignUpInfoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            signUpLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    val loginViewModel = LoginViewModel()
                    loginViewModel.sendOtpService(requestObject)
                        .observe(this, Observer<JSONObject?> { currencyPojos ->
                            if (currencyPojos != null) {
                                dismissProgressBar()
                                parseGenerateOtpResponse(currencyPojos)
                            }
                        })
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rlLoginMainLayout,
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
            //Log.e("Login OTP RES", obj.toString())
            submitFlag = false
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
                }
                launchOTPVerifyActivity()
            } else {
                CommonClass.showToastMessage(
                    context,
                    binding.rlLoginMainLayout,
                    obj.getString("message"),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchOTPVerifyActivity() {
        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra("mobileNo", binding.etMobileNo.text.toString())
        intent.putExtra("otp", otp)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun logInService() {
        try {
            hideKeyboard()
            if (validate()) {
                if (AppUtils.isConnectedToInternet(context)) {
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "phoneNumber",
                        binding.etMobileNo.text.toString()
                    )
                    requestObject.addProperty("password", binding.etPassword.text.toString())
                    requestObject.addProperty("preferredLanguage", language)
                    //Log.e("requestObj", requestObject.toString())
                    showProgressBar()
                    val loginViewModel = LoginViewModel()
                    loginViewModel.makeLogin(requestObject)
                        .observe(this, { jsonObject ->
                            //Log.e("jsonObject", jsonObject.toString() + "")
                            if (jsonObject != null) {
                                parseSignInResponse(jsonObject)
                            }
                        })
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rlLoginMainLayout,
                        resources.getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseSignInResponse(obj: JSONObject) {
        try {
            submitFlag = false
            //Log.e("LoginResponse", obj.toString())
            if (obj.has("data") && !obj.isNull("data")) {
                preferenceHelper!!.saveValueToSharedPrefs(
                    AppConstant.KEY_USER_NAME,
                    binding.etMobileNo.text.toString().trim()
                )
                if (binding.etPassword.text.toString().trim().isNotEmpty()) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_USER_PWD,
                        binding.etPassword.text.toString().trim()
                    )
                }
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

                //handleResponse()
                val mainActivityModel = MainActivityViewModel()
                mainActivityModel.getProductList(
                    preferenceHelper!!.getValueFromSharedPrefs(
                        AppConstant.KEY_TOKEN
                    )!!
                )?.observeForever {
                    dismissProgressBar()
                    parseProductList(it)
                }
            } else {
                CommonClass.showToastMessage(
                    context,
                    binding.rlLoginMainLayout,
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
                    launchHomeActivity()
                } else {
                    CommonClass.handleErrorResponse(context, jsonObject, binding.rlLoginMainLayout)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchHomeActivity() {
        try {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleResponse() {
        try {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validate(): Boolean {
        var flag = true
        try {
            if (binding.etMobileNo.text.toString().isEmpty()) {
                binding.etMobileNo.requestFocus()
                binding.etMobileNo.error = resources.getString(R.string.required)
                flag = false
            } else if (binding.etPassword.text.toString().isEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = resources.getString(R.string.required)
                flag = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        submitFlag = flag
        return flag
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

    override fun hideKeyboard() {
        try {
            val view = currentFocus
            if (view != null) {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }
}