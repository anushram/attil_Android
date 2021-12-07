package com.develop.sns.signup.password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivitySignUpPasswordBinding
import com.develop.sns.signup.dto.SignUpDto
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper


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
                handleResponse()
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

}