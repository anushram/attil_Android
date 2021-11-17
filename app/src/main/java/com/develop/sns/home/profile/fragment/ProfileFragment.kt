package com.develop.sns.home.profile.fragment

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.develop.sns.MainActivity
import com.develop.sns.R
import com.develop.sns.databinding.FragmentProfileBinding
import com.develop.sns.home.profile.LanguageSelectionActivity
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper


class ProfileFragment : Fragment() {

    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClassReference()
        handleUiElement()
    }

    private fun initClassReference() {
        try {
            if (preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID) == AppConstant.LANGUAGE_TYPE_TAMIL) {
                binding.tvLanguage.text = getString(R.string.tamil_title)
            } else {
                binding.tvLanguage.text = getString(R.string.english_title)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnLanguage.setOnClickListener {
                launchLanguageSettingActivity()
            }

            binding.lnLogout.setOnClickListener {
                showSignOutDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
            }
        }


    private fun launchLanguageSettingActivity() {
        try {
            val intent = Intent(context, LanguageSelectionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            resultLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSignOutDialog() {
        try {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setMessage(resources.getString(R.string.sign_out_title))
            builder.setCancelable(true)
            builder.setPositiveButton(
                resources.getString(R.string.yes),
                DialogInterface.OnClickListener { dialog, id ->
                    logoutService()
                    dialog.cancel()
                })
            builder.setNegativeButton(
                resources.getString(R.string.no),
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            val alert: AlertDialog = builder.create()
            alert.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logoutService() {
        try {
            val languageId: Int =
                preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            val language: String =
                preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_LANGUAGE)!!

            preferenceHelper!!.clear()
            preferenceHelper!!.saveIntValueToSharedPrefs(AppConstant.KEY_LANGUAGE_ID, languageId)
            preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_LANGUAGE, language)

            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().overridePendingTransition(0, 0)
            requireActivity().finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object
}