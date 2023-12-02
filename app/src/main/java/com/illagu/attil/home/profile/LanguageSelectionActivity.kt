package com.illagu.attil.home.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.illagu.attil.R
import com.illagu.attil.SubModuleActivity
import com.illagu.attil.databinding.ActivityLanguageSelectionBinding
import com.illagu.attil.home.HomeActivity
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.ApplicationManager
import java.util.*


class LanguageSelectionActivity : SubModuleActivity() {

    private val TAG = LanguageSelectionActivity::class.java.simpleName
    private val context: Context = this@LanguageSelectionActivity

    private val binding by lazy { ActivityLanguageSelectionBinding.inflate(layoutInflater) }

    private var selectedLanguage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        initToolBar()
        setUpUiElement()
        handleUiElement()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title = resources.getString(R.string.language_title)
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

    private fun setUpUiElement() {
        try {
            languageId = preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            //Log.e("SetUpUI", languageId.toString())
            setLanguageType(languageId)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.rgLanguage.setOnPositionChangedListener { position ->
                if (position == 0) {
                    selectedLanguage = AppConstant.LANGUAGE_TYPE_TAMIL
                } else if (position == 1) {
                    selectedLanguage = AppConstant.LANGUAGE_TYPE_ENGLISH
                }
                //Log.e("onPosChange", languageId.toString())
                //Log.e("onPosChangeSL", selectedLanguage.toString())
                if (languageId != selectedLanguage) {
                    performLanguageSelection()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setLanguageType(languageId: Int) {
        if (languageId == AppConstant.LANGUAGE_TYPE_TAMIL) {
            binding.rgLanguage.setPosition(0, true)
        } else {
            binding.rgLanguage.setPosition(1, true)
        }
    }

    private fun performLanguageSelection() {
        preferenceHelper!!.saveIntValueToSharedPrefs(AppConstant.KEY_LANGUAGE_ID, selectedLanguage)
        AppConstant.LANGUAGE_ID = selectedLanguage
        when (selectedLanguage) {
            AppConstant.LANGUAGE_TYPE_ENGLISH -> {
                preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_LANGUAGE, "en")
                setLangRecreate("en")
                AppConstant.LANGUAGE = "en"
            }
            AppConstant.LANGUAGE_TYPE_TAMIL -> {
                preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_LANGUAGE, "ta")
                setLangRecreate("ta")
                AppConstant.LANGUAGE = "ta"
            }
            else -> {
                preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_LANGUAGE, "en")
                setLangRecreate("en")
                AppConstant.LANGUAGE = "en"
            }
        }
        handleResponse()
        languageId = selectedLanguage
    }

    private fun setLangRecreate(langVal: String) {
        try {
            val config = baseContext.resources.configuration
            val locale = Locale(langVal)
            Locale.setDefault(locale)
            config.locale = locale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

            /* val resources: Resources = context.resources
             val configuration: Configuration = resources.configuration
             val locale = Locale(langVal)
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                 val localeList = LocaleList(locale)
                 LocaleList.setDefault(localeList)
                 configuration.setLocales(localeList)
             } else {
                 configuration.locale = locale
             }
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                 context.createConfigurationContext(configuration)
             } else {
                 resources.updateConfiguration(configuration, resources.displayMetrics)
             }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleResponse() {
        try {
            if (selectedLanguage != languageId) {
                if (HomeActivity().fa != null) {
                    HomeActivity().fa!!.finish()
                }
                reStartApp()
            } else {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun reStartApp() {
        try {
            val intent = baseContext.packageManager
                .getLaunchIntentForPackage(baseContext.packageName)
            intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            ApplicationManager.instance.setUpPhoneLanguage()
            startActivity(intent)
            finishAffinity()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}