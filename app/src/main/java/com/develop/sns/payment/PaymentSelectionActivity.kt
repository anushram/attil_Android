package com.develop.sns.payment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import com.develop.sns.R
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.SubModuleActivity
import com.develop.sns.address.AddressViewModel
import com.develop.sns.address.dto.AddressListDto
import com.develop.sns.cart.adapter.AddressItemListAdapter
import com.develop.sns.databinding.ActivityAddressSelectionBinding
import com.develop.sns.databinding.ActivityPaymentSelectionBinding
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.JsonObject
import org.json.JSONObject
import java.lang.String
import kotlin.Array
import kotlin.Boolean
import kotlin.Exception
import kotlin.Int
import kotlin.IntArray
import kotlin.arrayOf
import kotlin.assert
import kotlin.getValue
import kotlin.lazy
import kotlin.toString


class PaymentSelectionActivity : SubModuleActivity() {

    private val context: Context = this@PaymentSelectionActivity
    private val binding by lazy { ActivityPaymentSelectionBinding.inflate(layoutInflater) }

    private lateinit var paymentViewModel: PaymentViewModel

    private var totalAmount = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initToolBar()
        getIntentValue()
        initClassReference()
        handleUiElement()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title =
                getString(R.string.pay_using)
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
            totalAmount = intent.getFloatExtra("totalAmount", 0F);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            paymentViewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

            binding.tvTotalAmount.text = getString(R.string.Rs)
                .plus(" ")
                .plus("%.2f".format(totalAmount))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}