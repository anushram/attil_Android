package com.illagu.attil.payment

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
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser.parseString
import com.illagu.attil.R
import com.illagu.attil.SubModuleActivity
import com.illagu.attil.address.dto.AddressListDto
import com.illagu.attil.databinding.ActivityPaymentSelectionBinding
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.AppUtils
import com.illagu.attil.utils.CommonClass
import com.paytm.pgsdk.Constants.CALLBACK_URL
import com.paytm.pgsdk.Constants.CHANNEL_ID
import com.paytm.pgsdk.Constants.CHECKSUMHASH
import com.paytm.pgsdk.Constants.CUST_ID
import com.paytm.pgsdk.Constants.INDUSTRY_TYPE_ID
import com.paytm.pgsdk.Constants.MID
import com.paytm.pgsdk.Constants.ORDER_ID
import com.paytm.pgsdk.Constants.TXN_AMOUNT
import com.paytm.pgsdk.Constants.TXN_TOKEN
import com.paytm.pgsdk.Constants.WEBSITE
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
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

    private var mid = ""
    private var orderId = ""
    private var userId = ""
    private var txnToken = ""
    private var totalPaymentCost = ""
    private var callbackUrl = ""
    private var checkSumHash = ""
    private var channelId = ""


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

                        if (dataPaymentObject.has("mid") && !dataPaymentObject.isNull("mid")) {
                            mid = dataPaymentObject.getString("mid")
                        }

                        if (dataPaymentObject.has("userId") && !dataPaymentObject.isNull("userId")) {
                            userId = dataPaymentObject.getString("userId")
                        }

                        if (dataPaymentObject.has("orderId") && !dataPaymentObject.isNull("orderId")) {
                            orderId = dataPaymentObject.getString("orderId")
                        }

                        if (dataPaymentObject.has("txnToken") && !dataPaymentObject.isNull("txnToken")) {
                            txnToken = dataPaymentObject.getString("txnToken")
                        }

                        if (dataPaymentObject.has("totalCost") && !dataPaymentObject.isNull("totalCost")) {
                            totalPaymentCost = dataPaymentObject.getString("totalCost")
                        }

                        if (dataPaymentObject.has("callbackUrl") && !dataPaymentObject.isNull("callbackUrl")) {
                            callbackUrl = dataPaymentObject.getString("callbackUrl")
                        }

                        if (dataPaymentObject.has("checkSumHash") && !dataPaymentObject.isNull("checkSumHash")) {
                            checkSumHash = dataPaymentObject.getString("checkSumHash")
                        }

                        if (dataPaymentObject.has("channelId") && !dataPaymentObject.isNull("channelId")) {
                            channelId = dataPaymentObject.getString("channelId")
                        }

                    }
                }
            }
            if (paymentMode == "cod") {
                deliverNotification()
            } else {
                initGateWay()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deliverNotification() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = parseString(dataPaymentObject.toString()) as JsonObject
                requestObject.addProperty("customerId", userId)
                requestObject.addProperty("orderObjectId", orderId)
                requestObject.addProperty("orderId", orderId)
                requestObject.addProperty("sentBy", "user")
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

    private fun initGateWay() {
        try {

            val paramMap = HashMap<String, String>()
            paramMap[MID] = mid
            paramMap[ORDER_ID] = orderId
            paramMap[TXN_TOKEN] = txnToken
            paramMap[TXN_AMOUNT] = totalPaymentCost
            paramMap[CALLBACK_URL] = callbackUrl
            paramMap[CHECKSUMHASH] = checkSumHash
            paramMap[CUST_ID] = userId
            paramMap[CHANNEL_ID] = channelId
            paramMap[INDUSTRY_TYPE_ID] = "Retail"
            paramMap[WEBSITE] = ""

            val paytmOrder =
                PaytmOrder(orderId, mid, txnToken, totalPaymentCost, callbackUrl)

            /*val paytmOrder =
                PaytmOrder(paramMap)*/

            /*paytmPGService.initialize(paytmOrder, null)
            paytmPGService.startPaymentTransaction(
                this,
                true,
                true,
                object : PaytmPaymentTransactionCallback {
                    override fun onTransactionResponse(inResponse: Bundle?) {
                        TODO("Not yet implemented")
                        Log.d("TAG", "onTransactionResponse: $inResponse")
                        val ORDERID = inResponse!!.getString("ORDERID")
                        Log.d("TAG", "onTransactionResponse: $ORDERID")
                    }

                    override fun networkNotAvailable() {
                        Log.d("TAG", "networkNotAvailable: ");
                    }

                    override fun onErrorProceed(error: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun clientAuthenticationFailed(inErrorMessage: String?) {
                        TODO("Not yet implemented")
                        Log.d("TAG", "clientAuthenticationFailed: ")
                    }

                    override fun someUIErrorOccurred(inErrorMessage: String?) {
                        Log.d("TAG", "someUIErrorOccurred: ")
                    }

                    override fun onErrorLoadingWebPage(
                        iniErrorCode: Int,
                        inErrorMessage: String?,
                        inFailingUrl: String?
                    ) {
                        Log.d("TAG", "onErrorLoadingWebPage: ")
                    }

                    override fun onBackPressedCancelTransaction() {
                        Log.d("TAG", "onBackPressedCancelTransaction: ")
                    }

                    override fun onTransactionCancel(inErrorMessage: String?, inResponse: Bundle?) {
                        Log.d("TAG", "onTransactionCancel: $inErrorMessage")
                        Log.d("TAG", "onTransactionCancel: $inResponse")
                    }

                })*/

            val transactionManager =
                TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {
                    override fun onTransactionResponse(bundle: Bundle?) {
                        Toast.makeText(
                            this@PaymentSelectionActivity,
                            "Response (onTransactionResponse) : " + bundle.toString(),
                            Toast.LENGTH_SHORT
                        ).show();
                        Log.e("Response", bundle.toString())
                    }

                    override fun networkNotAvailable() {}
                    override fun onErrorProceed(s: String) {}
                    override fun clientAuthenticationFailed(s: String) {}
                    override fun someUIErrorOccurred(s: String) {}
                    override fun onErrorLoadingWebPage(i: Int, s: String, s1: String) {}
                    override fun onBackPressedCancelTransaction() {}
                    override fun onTransactionCancel(s: String, bundle: Bundle) {}
                })

            transactionManager.setAppInvokeEnabled(false)
            transactionManager.setShowPaymentUrl("https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage")
            transactionManager.startTransaction(this, REQUEST_CHECK)

        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Companion.REQUEST_CHECK && data != null) {
            Toast.makeText(
                this,
                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
                Toast.LENGTH_SHORT
            ).show()
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

    private var launcher =
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

    companion object {
        private const val REQUEST_CHECK = 100
    }
}