package com.develop.sns.signup.info

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivitySignUpBinding
import com.develop.sns.listener.AppUserListener
import com.develop.sns.signup.dto.SignUpDto
import com.develop.sns.signup.mobileno.SignUpMobileActivity
import com.develop.sns.signup.userdetail.SignUpUserDetailActivity
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import com.talentmicro.icanrefer.dto.ModuleDto
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class SignUpInfoActivity : SubModuleActivity(), AppUserListener {

    private val context: Context = this@SignUpInfoActivity
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }

    override var preferenceHelper: PreferenceHelper? = null
    private val dataList: ArrayList<ModuleDto> = ArrayList()
    var selectedPosition: Int = 0
    var isCompleted: Boolean = false
    var signUpDto: SignUpDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        initToolBar()
        initClassReference()
        populateList()
        handleUiElement()
        checkForSubmitButton()
    }


    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title = resources.getString(R.string.register)
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

    private fun initClassReference() {
        try {
            signUpDto = SignUpDto()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.btnCreate.setOnClickListener {
                createAccount()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateList() {
        dataList.clear()
        val moduleDto = ModuleDto()
        moduleDto.moduleTitle = "Mobile Number"
        dataList.add(moduleDto)
        val moduleDto1 = ModuleDto()
        moduleDto1.moduleTitle = "User Details"
        dataList.add(moduleDto1)
        binding.lvList.layoutManager = LinearLayoutManager(this)
        binding.lvList.adapter = InfoListAdapter(context, dataList, this@SignUpInfoActivity)

    }

    private fun checkForSubmitButton() = try {
        if (dataList.get(0).isCompleted == 1 && dataList.get(1).isCompleted == 1) {
            isCompleted = true
            binding.btnCreate.isEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.btnCreate.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.button_color
                        )
                    )
            } else {
                binding.btnCreate.setBackgroundResource(R.drawable.boundary_primary)
            }
            binding.btnCreate.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            binding.btnCreate.isEnabled = false
            isCompleted = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.btnCreate.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_grey_300))
            } else {
                binding.btnCreate.setBackgroundResource(R.drawable.boundary_button_gray)
            }
            binding.btnCreate.setTextColor(ContextCompat.getColor(context, R.color.text_color))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun selectItem(position: Int) {
        try {
            selectedPosition = position
            if (position == 0) {
                launchMobileNoActivity()
            } else if (position == 1) {
                if (dataList.get(0).isCompleted == 1) {
                    launchSignUpUserDetailsActivity()
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rootView,
                        getString(R.string.sign_up_warning),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var signUpMobNoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                signUpDto = data?.getSerializableExtra("signUpDto") as SignUpDto?
                val moduleDto: ModuleDto = dataList.get(selectedPosition)
                moduleDto.isCompleted = 1
                moduleDto.mobileNo =
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_MOBILE_NO)
                dataList.set(selectedPosition, moduleDto)
                binding.lvList.adapter?.notifyDataSetChanged()
                checkForSubmitButton()
            }
        }

    private fun launchMobileNoActivity() {
        try {
            val intent = Intent(context, SignUpMobileActivity::class.java)
            intent.putExtra("signUpDto", signUpDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            signUpMobNoLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var signUpUserDetailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                signUpDto = data?.getSerializableExtra("signUpDto") as SignUpDto?
                val moduleDto: ModuleDto = dataList.get(selectedPosition)
                moduleDto.isCompleted = 1
                moduleDto.userId =
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                dataList.set(selectedPosition, moduleDto)
                binding.lvList.adapter?.notifyDataSetChanged()
                checkForSubmitButton()
            }
        }

    private fun launchSignUpUserDetailsActivity() {
        try {
            val intent = Intent(context, SignUpUserDetailActivity::class.java)
            intent.putExtra("signUpDto", signUpDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            signUpUserDetailLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createAccount() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty("phoneVerficationId", signUpDto?.id)
                requestObject.addProperty("phoneNumber", signUpDto?.mobileNo)
                requestObject.addProperty("username", signUpDto?.userId)
                requestObject.addProperty("password", signUpDto?.password)
                requestObject.addProperty("isPassword", signUpDto?.isPassword)
                requestObject.addProperty("gender", signUpDto?.gender)
                requestObject.addProperty("role", "user")
                requestObject.addProperty("deviceModel", getDeviceName())
                requestObject.addProperty("os", "Android")
                requestObject.addProperty("preferredLanguage", language)
                requestObject.addProperty(
                    "udid",
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_DEVICE_ID)!!
                )
                requestObject.addProperty(
                    "deviceToken",
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_GCM_ID)!!
                )
                //Log.e("Request Object", requestObject.toString())
                showProgressBar()
                val signUpViewModel = SignUpViewModel()
                signUpViewModel.createAccount(requestObject).observe(this, { currencyPojos ->
                    if (currencyPojos != null) {
                        dismissProgressBar()
                        parseCreateAccountResponse(currencyPojos)
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseCreateAccountResponse(obj: JSONObject) {
        try {
            //Log.e("Response", obj.toString())
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

    private fun handleResponse() {
        try {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase(Locale.getDefault())
                .startsWith(manufacturer.lowercase(Locale.getDefault()))
        ) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
}