package com.illagu.attil.signup.password

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.illagu.attil.MainActivityViewModel
import com.illagu.attil.R
import com.illagu.attil.SubModuleActivity
import com.illagu.attil.databinding.ActivitySignUpPasswordBinding
import com.illagu.attil.login.otp.OtpViewModel
import com.illagu.attil.signup.dto.SignUpDto
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.AppUtils
import com.illagu.attil.utils.CommonClass
import com.illagu.attil.utils.PreferenceHelper
import org.json.JSONObject


class SignUpPasswordActivity : SubModuleActivity() {

    private val context: Context = this@SignUpPasswordActivity
    private val binding by lazy { ActivitySignUpPasswordBinding.inflate(layoutInflater) }

    override var preferenceHelper: PreferenceHelper? = null
    private var submitFlag = false
    private var isSkip = false
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
            binding.lnToolbar.toolbar.title = resources.getString(R.string.password)
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
            binding.btnNext.setOnClickListener {
                handleBackData()
            }

            binding.cbNewPassword.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                val start: Int
                val end: Int
                //////Log.i("inside checkbox chnge", "" + isChecked);
                if (!isChecked) {
                    start = binding.etNewPassword.selectionStart
                    end = binding.etNewPassword.selectionEnd
                    binding.etNewPassword.transformationMethod = PasswordTransformationMethod()
                    binding.etNewPassword.setSelection(start, end)
                } else {
                    start = binding.etNewPassword.selectionStart
                    end = binding.etNewPassword.selectionEnd
                    binding.etNewPassword.transformationMethod = null
                    binding.etNewPassword.setSelection(start, end)
                }
            })

            binding.cbConfirmPassword.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                val start: Int
                val end: Int
                //////Log.i("inside checkbox chnge", "" + isChecked);
                if (!isChecked) {
                    start = binding.etConfirmPassword.selectionStart
                    end = binding.etConfirmPassword.selectionEnd
                    binding.etConfirmPassword.transformationMethod = PasswordTransformationMethod()
                    binding.etConfirmPassword.setSelection(start, end)
                } else {
                    start = binding.etConfirmPassword.selectionStart
                    end = binding.etConfirmPassword.selectionEnd
                    binding.etConfirmPassword.transformationMethod = null
                    binding.etConfirmPassword.setSelection(start, end)
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleBackData() {
        try {
            if (validatePassword()) {
                signUpDto?.password = binding.etNewPassword.text.toString()
                signUpDto?.isPassword = true
                createAccountService()
               // handleResponse()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validatePassword(): Boolean {
        var flag = true
        try {
            if (binding.etNewPassword.text.toString().isEmpty()) {
                binding.etNewPassword.requestFocus()
                binding.etNewPassword.error = resources.getString(R.string.required)
                flag = false
            } else if (binding.etNewPassword.text.toString().trim().length < 6
                || binding.etNewPassword.text.toString().trim().length > 24
            ) {
                binding.etNewPassword.requestFocus()
                binding.etNewPassword.error =
                    resources.getString(R.string.password_length_validation)
                flag = false
            } else if (CommonClass.validatePassword(
                    binding.etNewPassword.text.toString().trim()
                ) == false
            ) {
                binding.etNewPassword.requestFocus()
                binding.etNewPassword.error =
                    resources.getString(R.string.password_length_validation)
                flag = false
            } else if (binding.etConfirmPassword.text.toString().length == 0) {
                binding.etConfirmPassword.requestFocus()
                binding.etConfirmPassword.error = resources.getString(R.string.required)
                flag = false
            } else if (binding.etNewPassword.text.toString()
                    .trim() != binding.etConfirmPassword.text.toString().trim()
            ) {
                binding.etConfirmPassword.requestFocus()
                binding.etConfirmPassword.error =
                    resources.getString(R.string.new_password_match_validation)
                flag = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        submitFlag = flag
        return flag
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_skip, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuSkip: MenuItem = menu.findItem(R.id.action_skip)
        menuSkip.isVisible = true
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_skip -> {
                signUpDto?.isPassword = false
                signUpDto?.password = ""
                handleResponse()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

//create password service
    private fun createAccountService() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                try {
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "phoneVerficationId",
                        preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_OTP_ID)
                    )
                    requestObject.addProperty("phoneNumber",  preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_MOBILE_NO))
                    requestObject.addProperty("username", preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_MOBILE_NO )+"@")
                    requestObject.addProperty("password", binding.etNewPassword.text.toString())
                    requestObject.addProperty("isPassword", true)
                    requestObject.addProperty("gender", "other")
                    requestObject.addProperty("preferredLanguage", language)
                    requestObject.addProperty("deviceModel", Build.MODEL)
                    requestObject.addProperty("deviceToken", token)
                    requestObject.addProperty("os", Build.VERSION.RELEASE)
                    //Log.e("Request", requestObject.toString())
                    showProgressBar()
                    val accountViewModel = SingUpPasswordViewModel(application)
                    accountViewModel.createAccountService(requestObject).observe(this, Observer {
                            currencyPojos ->
                        Log.e("currencyPojos", currencyPojos.toString() + "")
                        if (currencyPojos != null) {
                            dismissProgressBar()
                            parseCreateAccountResponse(currencyPojos)
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
    private fun parseCreateAccountResponse(obj: JSONObject){
        try {
            submitFlag = false
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataObject = obj.getJSONObject("data")
                        if (dataObject.has("access_token") && dataObject.getString("access_token")!="") {
                          //  val accesstoken = dataObject.getString("access_token")
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_TOKEN,
                                dataObject.getString("access_token")
                            )
                        }
                        if (dataObject.has("userId") && dataObject.getString("userId")!="") {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_USER_ID,
                                dataObject.getString("userId")
                            )
                        }
                        if (dataObject.has("username") && dataObject.getString("username")!="") {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_NAME,
                                dataObject.getString("username")
                            )
                        }

                        handleResponse()

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

}