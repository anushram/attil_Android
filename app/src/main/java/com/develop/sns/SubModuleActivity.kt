package com.develop.sns


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.develop.sns.databinding.ActivitySubModuleBinding
import com.develop.sns.databinding.ProgressBarLayoutBinding
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper
import java.util.*


open class SubModuleActivity : ModuleActivity() {

    private val context: SubModuleActivity = this@SubModuleActivity

    private val binding by lazy { ActivitySubModuleBinding.inflate(layoutInflater) }

    open var preferenceHelper: PreferenceHelper? = null
    var token: String? = null
    open var languageId = 0
    open var language = ""
    var lnProgressBar: LinearLayout? = null
    var progressBarLayoutBinding: ProgressBarLayoutBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpScreen()
        getPreferenceValues()
    }

    private fun setUpScreen() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = ContextCompat.getColor(
                    context,
                    R.color.accent
                )
                window.navigationBarColor = ContextCompat.getColor(
                    context,
                    R.color.black
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPreferenceValues() {
        try {
            preferenceHelper = PreferenceHelper(context)
            token = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)
            languageId = preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            language = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_LANGUAGE)!!
            Log.e("SubMod LngCode", language)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    open fun hideKeyboard() {
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

    open fun showProgressBar() {
        try {
            this.progressBarLayoutBinding!!.root.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun dismissProgressBar() {
        try {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            this.progressBarLayoutBinding!!.root.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initialiseProgressBar(progressbarId: ProgressBarLayoutBinding) {
        try {
            this.progressBarLayoutBinding = progressbarId
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 101
    }
}