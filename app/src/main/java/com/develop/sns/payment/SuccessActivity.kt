package com.develop.sns.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.text.HtmlCompat
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivitySuccessBinding
import com.develop.sns.utils.Presets
import java.util.*

class SuccessActivity : SubModuleActivity() {

    private val binding by lazy { ActivitySuccessBinding.inflate(layoutInflater) }

    private var timer: Timer? = null

    private lateinit var myAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intentValue
        initClassReference()
        timerFunctionality()
    }

    private val intentValue: Unit
        get() {
            try {
                val intent: Intent = intent
                if (intent.hasExtra("message")) {
                    val message: String = intent.getStringExtra("message")!!
                    binding.tvSuccess.visibility = View.VISIBLE
                    binding.ivSuccess.visibility = View.VISIBLE
                    binding.tvSuccess.text =
                        HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY);
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun initClassReference() {
        try {
            myAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_in_out)
            binding.ivSuccess.startAnimation(myAnim)
            binding.konfettiView.start(Presets.rain())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun timerFunctionality() {
        try {
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    handleResponse()
                }
            }, 5000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        handleResponse()
    }

    private fun handleResponse() {
        try {
            timer!!.cancel()
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            this@SuccessActivity.finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}