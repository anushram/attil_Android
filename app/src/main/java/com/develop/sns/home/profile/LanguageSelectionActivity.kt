package com.develop.sns.home.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityLanguageSelectionBinding
import com.develop.sns.databinding.FragmentProfileBinding
import com.develop.sns.home.HomeActivity

class LanguageSelectionActivity : SubModuleActivity() {

    private val TAG = LanguageSelectionActivity::class.java.simpleName
    private val context: Context = this@LanguageSelectionActivity

    private val binding by lazy { ActivityLanguageSelectionBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}