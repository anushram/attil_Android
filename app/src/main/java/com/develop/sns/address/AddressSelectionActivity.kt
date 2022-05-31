package com.develop.sns.address

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.cart.adapter.CartItemListAdapter
import com.develop.sns.cart.dto.CartListDto
import com.develop.sns.databinding.ActivityAddressSelectionBinding
import com.develop.sns.home.details.ItemDetailsActivity
import com.develop.sns.home.details.ItemDetailsViewModel
import com.develop.sns.home.offers.dto.ProductDto
import com.develop.sns.home.offers.dto.ProductPriceDto
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AddressSelectionActivity : SubModuleActivity() {

    private val context: Context = this@AddressSelectionActivity
    private val binding by lazy { ActivityAddressSelectionBinding.inflate(layoutInflater) }

    private lateinit var addressViewModel: AddressViewModel

    var totalAmount = 0F
    var deliveryCharge = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initToolBar()
        getIntentValue()
        initClassReference()
        handleUiElement()
        getSavedAddress()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title =
                resources.getString(R.string.adress)
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
            totalAmount = intent.getFloatExtra("total", 0F);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

            binding.tvTotalAmount.text =
                getString(R.string.total_item_price).plus(" ").plus(getString(R.string.Rs))
                    .plus(" ")
                    .plus("%.2f".format(totalAmount))

            val minFreeDelivery =
                preferenceHelper?.getIntFromSharedPrefs(AppConstant.KEY_MIN_FREE_DELIVERY)
                    ?.toFloat()

            if (totalAmount >= minFreeDelivery!!.toFloat()) {
                deliveryCharge = "FREE"
                binding.tvDeliveryCharge.text = deliveryCharge
            } else {
                deliveryCharge =
                    preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_DELIVERY_COST)
                        .toString()
                binding.tvDeliveryCharge.text =
                    getString(R.string.Rs).plus(" ").plus("%.2f".format(deliveryCharge))
            }

            binding.tvDeliveryChargeInfo.text =
                getString(R.string.purchase_above).plus(" ").plus(getString(R.string.Rs)).plus(" ")
                    .plus("%.2f".format(minFreeDelivery)).plus(" ")
                    .plus(getString(R.string.free_delivery))

            val packingCharges =
                preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_PACKAGE_COST).toFloat()
            binding.tvPackingCharges.text =
                getString(R.string.packing_charges).plus(" ").plus(getString(R.string.Rs)).plus(" ")
                    .plus("%.2f".format(packingCharges))
            binding.tvTotalPrice.text =
                getString(R.string.Rs).plus(" ").plus("%.2f".format(totalAmount))
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

    private fun getSavedAddress() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty(
                    "_id",
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                )
                showProgressBar()

                addressViewModel.getSavedAddressList(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this) { jsonObject ->
                    dismissProgressBar()
                    parseSavedAddressResponse(jsonObject)
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

    private fun parseSavedAddressResponse(obj: JSONObject) {
        try {
            Log.e("SavedAddress", obj.toString())
            binding.rootView.visibility = View.VISIBLE
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val itemObject = dataArray.getJSONObject(i)

                        }
                    }
                }
            } else {
                binding.lvProducts.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
            populateCartItemList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateCartItemList() {
        try {
            /*if (!cartItemList.isNullOrEmpty()) {
                binding.lvProducts.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                linearLayoutManager = LinearLayoutManager(context)
                binding.lvProducts.layoutManager = linearLayoutManager
                cartItemListAdapter =
                    CartItemListAdapter(context, cartItemList, this@AddressSelectionActivity)
                binding.lvProducts.adapter = cartItemListAdapter
            } else {
                binding.lvProducts.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            }
            calculateTotal(cartItemList)*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}