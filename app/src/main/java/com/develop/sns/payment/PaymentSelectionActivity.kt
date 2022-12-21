package com.develop.sns.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.develop.sns.MainActivityViewModel
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.address.dto.AddressListDto
import com.develop.sns.databinding.ActivityPaymentSelectionBinding
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser.parseString
import org.json.JSONObject


class PaymentSelectionActivity : SubModuleActivity() {

    private val context: Context = this@PaymentSelectionActivity
    private val binding by lazy { ActivityPaymentSelectionBinding.inflate(layoutInflater) }

    private lateinit var paymentViewModel: PaymentViewModel

    private var totalCost = 0F
    private var productCost = 0F
    private var packageCost = 0F
    private var deliveryCost = 0F
    private var reductionAmount = 0F
    private var deliveryAddressId = ""
    private var lat = ""
    private var lng = ""
    private var paymentMode = ""
    private lateinit var cartItemArray: JsonArray
    private var selectedAddressListDto: AddressListDto? = null
    private var isCurrLoc = false

    private lateinit var dataPaymentObject: JSONObject


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
            totalCost = intent.getFloatExtra("totalCost", 0F);
            productCost = intent.getFloatExtra("productCost", 0F);
            packageCost = intent.getFloatExtra("packageCost", 0F);
            deliveryCost = intent.getFloatExtra("deliveryCost", 0F);
            reductionAmount = intent.getFloatExtra("reductionAmount", 0F);
            val cartItem = intent.getStringExtra("cart")
            val elem: JsonElement = parseString(cartItem)
            cartItemArray = elem.asJsonArray
            deliveryAddressId = intent.getStringExtra("deliveryAddressId")!!
            lat = intent.getStringExtra("lat")!!
            lng = intent.getStringExtra("lng")!!
            isCurrLoc = intent.getBooleanExtra("isCurrentLocation", false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            paymentViewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

            binding.tvTotalAmount.text = getString(R.string.Rs)
                .plus(" ")
                .plus("%.2f".format(totalCost))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnDebitCredit.setOnClickListener {
                paymentMode = "debitcard"
                initPayment()
            }
            binding.lnUpi.setOnClickListener {
                paymentMode = "upi"
                initPayment()
            }
            binding.lnCod.setOnClickListener {
                paymentMode = "cod"
                initPayment()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initPayment() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty(
                    "userId",
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                )
                requestObject.addProperty("paymentMode", paymentMode)
                requestObject.addProperty("totalCost", totalCost.toString())
                requestObject.addProperty("productCost", productCost.toString())
                requestObject.addProperty("packageCost", packageCost.toDouble())
                requestObject.addProperty("deliveryCost", deliveryCost.toString())
                requestObject.addProperty("reductionAmount", reductionAmount.toString())
                requestObject.add("cart", cartItemArray)
                requestObject.addProperty("deliveryAddressId", deliveryAddressId)
                val deliveryLocObj = JsonObject()
                deliveryLocObj.addProperty("lat", lat)
                deliveryLocObj.addProperty("lng", lng)
                requestObject.add("deliveryLocation", deliveryLocObj)
                requestObject.addProperty("isCurrentLocation", isCurrLoc)
                showProgressBar()

                paymentViewModel.initPayment(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this) { jsonObject ->
                    dismissProgressBar()
                    parseInitPayment(jsonObject)
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

    private fun parseInitPayment(obj: JSONObject) {
        try {
            Log.e("parseInitPayment", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        dataPaymentObject = obj.getJSONObject("data")
                    }
                }
            }
            deliverNotification()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deliverNotification() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = parseString(dataPaymentObject.toString()) as JsonObject
                requestObject.addProperty("type", "delivery")
                requestObject.addProperty(
                    "shopId",
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_SHOP_ID)
                )
                showProgressBar()

                paymentViewModel.deliverNotification(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this) { jsonObject ->
                    dismissProgressBar()
                    parseDeliverNotification(jsonObject)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseDeliverNotification(obj: JSONObject) {
        try {
            Log.e("parseDeliver", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    launchSuccessActivity()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchSuccessActivity() {
        try {
            val intent = Intent(context, SuccessActivity::class.java)
            intent.putExtra("message", "Ordered Successfully")
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            launcher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleResponse()
            }
        }

    private fun handleResponse() {
        try {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}