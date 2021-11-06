package com.develop.sns.signup.userdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivitySignUpUserDetailBinding
import com.develop.sns.signup.dto.SignUpDto
import com.develop.sns.signup.password.SignUpPasswordActivity
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import org.json.JSONObject
import android.text.Editable


class SignUpUserDetailActivity : SubModuleActivity() {

    private val context: Context = this@SignUpUserDetailActivity
    private val binding by lazy { ActivitySignUpUserDetailBinding.inflate(layoutInflater) }

    override var preferenceHelper: PreferenceHelper? = null
    private var submitFlag = false
    private var isValid = false;
    var signUpDto: SignUpDto? = null
    var segmentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        initToolBar()
        getIntentValue();
        initClassReference()
        handleUiElement()
    }

    private fun initToolBar() {
        try {
            (binding.lnToolbar.toolbar as Toolbar).title =
                resources.getString(R.string.user_details)
            setSupportActionBar(binding.lnToolbar.toolbar as Toolbar)
            assert(supportActionBar != null)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            (binding.lnToolbar.toolbar as Toolbar).navigationIcon = ContextCompat.getDrawable(
                context,
                R.drawable.ic_action_back
            )
            (binding.lnToolbar.toolbar as Toolbar).layoutDirection =
                View.LAYOUT_DIRECTION_LTR
            (binding.lnToolbar.toolbar as Toolbar).setNavigationOnClickListener { onBackPressed() }
        } catch (bug: Exception) {
            bug.printStackTrace()
        }
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            signUpDto = intent.getSerializableExtra("signUpDto") as SignUpDto?;
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            binding.tvSuccessText.text = getString(R.string.give_your_username)
            binding.tvSuccessText.setTextColor(ContextCompat.getColor(context, R.color.text_color))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnError.ivClose!!.setOnClickListener(View.OnClickListener { hideErrorMessage() })

            binding.etUserName.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                    if (submitFlag == false) {
                        submitFlag = true
                        checkUserNameAvailability()
                        return@OnEditorActionListener true
                    }
                }
                false
            })

            binding.etUserName.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // TODO Auto-generated method stub
                }

                override fun afterTextChanged(s: Editable) {
                    val data = s.toString()
                    if (data.isNotEmpty()) {
                        isValid = true
                    } else {
                        isValid = false
                        binding.tvSuccessText.text = getString(R.string.give_your_username)
                        binding.tvSuccessText.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_color
                            )
                        )
                    }
                }
            })

            binding.btnNext.setOnClickListener {
                if (isValid) {
                    signUpDto?.gender = binding.rgGender.getButton(binding.rgGender.position).text
                    launchPasswordActivity()
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rootView,
                        "Please enter valid Username and check for it's availability",
                        Toast.LENGTH_SHORT
                    )
                }
            }

            binding.rgGender.setOnPositionChangedListener { position ->
                segmentPosition = position
            }

            binding.rootView.viewTreeObserver.addOnGlobalLayoutListener {
                val rec = Rect()
                binding.rootView.getWindowVisibleDisplayFrame(rec)

                //finding screen height
                val screenHeight = binding.rootView.rootView.height

                //finding keyboard height
                val keypadHeight = screenHeight - rec.bottom

                if (keypadHeight > screenHeight * 0.15) {

                } else {
                    if (!isValid) {
                        if (binding.etUserName.text.isNotEmpty() && binding.etUserName.text.length == 10) {
                            if (!submitFlag) {
                                submitFlag = true
                                checkUserNameAvailability()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var signUpPasswordLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                signUpDto = data?.getSerializableExtra("signUpDto") as SignUpDto?
                handleResponse()
            }
        }

    private fun launchPasswordActivity() {
        try {
            val intent = Intent(context, SignUpPasswordActivity::class.java)
            intent.putExtra("signUpDto", signUpDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            signUpPasswordLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    private fun validate(): Boolean {
        var flag = true
        try {
            if (binding.etUserName.getText().toString().isEmpty()) {
                binding.etUserName.requestFocus()
                binding.etUserName.setError(resources.getString(R.string.required))
                flag = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        submitFlag = flag
        return flag
    }

    private fun checkUserNameAvailability() {
        try {
            if (validate()) {
                if (AppUtils.isConnectedToInternet(context)) {
                    val requestObject = JsonObject()
                    requestObject.addProperty("username", binding.etUserName.getText().toString())
                    requestObject.addProperty("preferredLanguage", language)
                    showProgressBar()
                    val signUpUserDetailViewModel = SignUpUserDetailViewModel()
                    signUpUserDetailViewModel.checkUserNameAvailability(
                        requestObject
                    )?.observe(this, Observer<JSONObject?> { jsonObject ->
                        Log.e("jsonObject", jsonObject.toString() + "")
                        if (jsonObject != null) {
                            dismissProgressBar()
                            parseUserNameResponse(jsonObject)
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

    private fun parseUserNameResponse(obj: JSONObject) {
        try {
            submitFlag = false
            Log.e("LoginResponse", obj.toString())
            if (obj.has("status")) {
                if (obj.getBoolean("status")) {
                    isValid = true
                    binding.tvSuccessText.text = getString(R.string.username_available)
                    binding.tvSuccessText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.md_green_A700
                        )
                    )
                    signUpDto?.userId = binding.etUserName.text.toString()
                    preferenceHelper?.saveValueToSharedPrefs(
                        AppConstant.KEY_USER_ID,
                        binding.etUserName.text.toString()
                    )
                } else {
                    isValid = false
                    binding.tvSuccessText.text = getString(R.string.username_not_available)
                    binding.tvSuccessText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.error_color
                        )
                    )
                    signUpDto?.userId = ""
                    preferenceHelper?.saveValueToSharedPrefs(
                        AppConstant.KEY_USER_ID,
                        ""
                    )
                }
            } else {
                isValid = false
                CommonClass.showToastMessage(
                    context,
                    binding.rootView,
                    obj.getString("message"),
                    Toast.LENGTH_SHORT
                );
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}