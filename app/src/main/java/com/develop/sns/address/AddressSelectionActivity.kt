package com.develop.sns.address

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.address.dto.AddressListDto
import com.develop.sns.address.listener.AddressListener
import com.develop.sns.cart.adapter.AddressItemListAdapter
import com.develop.sns.databinding.ActivityAddressSelectionBinding
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.gson.JsonObject
import org.json.JSONObject

class AddressSelectionActivity : SubModuleActivity(), AddressListener {

    private val context: Context = this@AddressSelectionActivity
    private val binding by lazy { ActivityAddressSelectionBinding.inflate(layoutInflater) }

    private lateinit var addressViewModel: AddressViewModel
    private lateinit var addressItemList: ArrayList<AddressListDto>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addressItemListAdapter: AddressItemListAdapter

    private var totalAmount = 0F
    private var deliveryCharge = 0F

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
            addressItemList = ArrayList()

            binding.tvTotalAmount.text =
                getString(R.string.total_item_price).plus(" ").plus(getString(R.string.Rs))
                    .plus(" ")
                    .plus("%.2f".format(totalAmount))

            val minFreeDelivery =
                preferenceHelper?.getIntFromSharedPrefs(AppConstant.KEY_MIN_FREE_DELIVERY)
                    ?.toFloat()
            Log.e("min", minFreeDelivery.toString());
            if (totalAmount >= minFreeDelivery!!.toFloat()) {
                binding.tvDeliveryCharge.text = getString(R.string.free)
            } else {
                deliveryCharge =
                    preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_DELIVERY_COST)
                        .toFloat()
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

            binding.btnAddAddress.setOnClickListener {
                binding.rgType.
            }
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
                        val dataObject = obj.getJSONObject("data")

                        if (dataObject.has("_id") && !dataObject.isNull("_id")) {
                            val id = dataObject.getString("_id")
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_ADDRESS_MAIN_ID,
                                id
                            )
                        }

                        if (dataObject.has("address") && !dataObject.isNull("address")) {
                            val addressObject = dataObject.getJSONArray("address")
                            Log.e("Address", addressObject.toString())
                            for (i in 0 until addressObject.length()) {
                                val itemObject = addressObject.getJSONObject(i)
                                val addressListDto = AddressListDto()
                                if (itemObject.has("geo") && !itemObject.isNull("geo")) {
                                    val geoObject = itemObject.getJSONObject("geo")
                                    if (geoObject.has("lat") && !geoObject.isNull("lat")) {
                                        addressListDto.lat = geoObject.getString("lat")
                                    }
                                    if (geoObject.has("lng") && !geoObject.isNull("lng")) {
                                        addressListDto.lng = geoObject.getString("lng")
                                    }
                                }

                                if (itemObject.has("createdAt") && !itemObject.isNull("createdAt")) {
                                    addressListDto.createdAt = itemObject.getString("createdAt")
                                }

                                if (itemObject.has("createdAtTZ") && !itemObject.isNull("createdAtTZ")) {
                                    addressListDto.createdAtTZ = itemObject.getString("createdAtTZ")
                                }

                                if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                    addressListDto._id = itemObject.getString("_id")
                                }

                                if (itemObject.has("street") && !itemObject.isNull("street")) {
                                    addressListDto.street = itemObject.getString("street")
                                }

                                if (itemObject.has("phoneNumber") && !itemObject.isNull("phoneNumber")) {
                                    addressListDto.phoneNumber = itemObject.getString("phoneNumber")
                                }

                                if (itemObject.has("doorNo") && !itemObject.isNull("doorNo")) {
                                    addressListDto.doorNo = itemObject.getString("doorNo")
                                }

                                if (itemObject.has("landmark") && !itemObject.isNull("landmark")) {
                                    addressListDto.landmark = itemObject.getString("landmark")
                                }

                                if (itemObject.has("pinCode") && !itemObject.isNull("pinCode")) {
                                    addressListDto.pinCode = itemObject.getString("pinCode")
                                }

                                if (itemObject.has("townORcity") && !itemObject.isNull("townORcity")) {
                                    addressListDto.townORcity = itemObject.getString("townORcity")
                                }
                                addressItemList.add(addressListDto)
                            }
                        }
                    }
                }
            } else {
                binding.lvProducts.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
            populateAddressItemList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateAddressItemList() {
        try {
            if (!addressItemList.isNullOrEmpty()) {
                binding.lvProducts.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                linearLayoutManager = LinearLayoutManager(context)
                binding.lvProducts.layoutManager = linearLayoutManager
                addressItemListAdapter =
                    AddressItemListAdapter(context, addressItemList, this@AddressSelectionActivity)
                binding.lvProducts.adapter = addressItemListAdapter
            } else {
                binding.lvProducts.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectItem(addressListDto: AddressListDto) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun edit(addressListDto: AddressListDto) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}