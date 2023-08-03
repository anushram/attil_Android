package com.illagu.attil.address

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
import com.illagu.attil.R
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
import com.illagu.attil.SubModuleActivity
import com.illagu.attil.address.dto.AddressListDto
import com.illagu.attil.address.listener.AddressListener
import com.illagu.attil.cart.adapter.AddressItemListAdapter
import com.illagu.attil.databinding.ActivityAddressSelectionBinding
import com.illagu.attil.payment.PaymentSelectionActivity
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.AppUtils
import com.illagu.attil.utils.CommonClass
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.lang.String
import kotlin.Array
import kotlin.Boolean
import kotlin.Exception
import kotlin.Int
import kotlin.IntArray
import kotlin.arrayOf
import kotlin.assert
import kotlin.lazy
import kotlin.toString


class AddressSelectionActivity : SubModuleActivity(), AddressListener {

    private val context: Context = this@AddressSelectionActivity
    private val binding by lazy { ActivityAddressSelectionBinding.inflate(layoutInflater) }

    private lateinit var addressViewModel: AddressViewModel
    private lateinit var addressItemList: ArrayList<AddressListDto>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var addressItemListAdapter: AddressItemListAdapter
    private lateinit var cartItemArray: JSONArray

    private var totalAmount = 0F
    private var deliveryCharge = 0F
    private var total = 0F
    private var packingCharges = 0F

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest

    private var latitude = "0"
    private var longitude = "0"

    private val REQUEST_FINE_LOCATION = 104
    private var isRadioClicked = false
    private var isCurrLoc = false
    private var isFromList = false

    private var selectedAddressListDto: AddressListDto? = null
    private var lastCheckedPosition = -1

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            try {
                val mLastLocation: Location? = locationResult.lastLocation
                if (mLastLocation != null) {
                    latitude = String.valueOf(mLastLocation.getLatitude())
                    longitude = String.valueOf(mLastLocation.getLongitude())
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_CURRENT_LATITUDE,
                        latitude
                    )
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_CURRENT_LONGITUDE,
                        longitude
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initToolBar()
        getIntentValue()
        initClassReference()
        initLocationReference()
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
            val cartItem = intent.getStringExtra("cart")
            cartItemArray = JSONArray(cartItem)
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
                deliveryCharge = (0).toFloat()
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

            packingCharges =
                preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_PACKAGE_COST).toFloat()
            binding.tvPackingCharges.text =
                getString(R.string.packing_charges).plus(" ").plus(getString(R.string.Rs)).plus(" ")
                    .plus("%.2f".format(packingCharges))
            total = deliveryCharge + packingCharges + totalAmount
            binding.tvTotalPrice.text =
                getString(R.string.Rs).plus(" ").plus("%.2f".format(total))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {

            binding.rbCurrentLocation.setOnTouchListener(OnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    isRadioClicked = true
                } else if (event.action == MotionEvent.ACTION_MOVE) {
                    isRadioClicked = true
                } else if (event.action == MotionEvent.ACTION_UP) {
                    isRadioClicked = true
                }
                false
            })


            binding.rgType.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId -> //Log.e("HandleUi", "Element");
                when (checkedId) {
                    R.id.rb_current_location -> {
                        if (isRadioClicked) {
                            isCurrLoc = true
                            isFromList = false;
                            addressItemListAdapter.lastCheckedPosition = -1
                            addressItemListAdapter.notifyItemRangeChanged(
                                0,
                                addressItemList.size - 1
                            )
                            checkLocationPermission()
                        }
                    }
                    else -> {
                    }
                }
            })

            binding.btnAddAddress.setOnClickListener {
                isRadioClicked = false
                binding.tvAddress.text = ""
                binding.tvAddress.visibility = View.GONE
                binding.rgType.clearCheck()
                latitude = ""
                longitude = ""
                launchAddNewAddressActivity(null)
            }

            binding.btnPayNow.setOnClickListener {
                if (!isCurrLoc && !isFromList) {
                    CommonClass.showToastMessage(
                        context,
                        binding.rootView,
                        "Please add / choose address",
                        Toast.LENGTH_SHORT
                    )
                } else {
                    if (isCurrLoc) {
                        selectedAddressListDto = null
                        findShopByCoordinates(latitude, longitude)
                    } else if (isFromList) {
                        findShopByCoordinates(
                            selectedAddressListDto!!.lat,
                            selectedAddressListDto!!.lng
                        )
                    }
                }
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

                                if (itemObject.has("landmark") && !itemObject.isNull("landmark")) {
                                    addressListDto.landmark = itemObject.getString("landmark")
                                }

                                if (itemObject.has("doorNo") && !itemObject.isNull("doorNo")) {
                                    addressListDto.doorNo = itemObject.getString("doorNo")
                                }

                                if (itemObject.has("phoneNumber") && !itemObject.isNull("phoneNumber")) {
                                    addressListDto.phoneNumber = itemObject.getString("phoneNumber")
                                }

                                if (itemObject.has("townORcity") && !itemObject.isNull("townORcity")) {
                                    addressListDto.townORcity = itemObject.getString("townORcity")
                                }

                                if (itemObject.has("pinCode") && !itemObject.isNull("pinCode")) {
                                    addressListDto.pinCode = itemObject.getString("pinCode")
                                }

                                if (itemObject.has("fullName") && !itemObject.isNull("fullName")) {
                                    addressListDto.fullName = itemObject.getString("fullName")
                                }

                                if (itemObject.has("updatedAt") && !itemObject.isNull("updatedAt")) {
                                    addressListDto.updatedAt = itemObject.getString("updatedAt")
                                }

                                if (itemObject.has("additionalPhoneNumber") && !itemObject.isNull("additionalPhoneNumber")) {
                                    addressListDto.additionalPhoneNumber =
                                        itemObject.getString("additionalPhoneNumber")
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
            this.selectedAddressListDto = addressListDto
            isRadioClicked = false
            binding.tvAddress.text = ""
            binding.tvAddress.visibility = View.GONE
            binding.rgType.clearCheck()
            latitude = ""
            longitude = ""
            isFromList = true
            isCurrLoc = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun edit(addressListDto: AddressListDto) {
        try {
            launchAddNewAddressActivity(addressListDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findShopByCoordinates(latitude: kotlin.String, longitude: kotlin.String) {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty("lat", "13.089174642337015")
                requestObject.addProperty("lng", "80.16765408199427")
                showProgressBar()

                addressViewModel.findShop(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this) { jsonObject ->
                    dismissProgressBar()
                    parseFindShopResponse(jsonObject)
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

    private fun parseFindShopResponse(obj: JSONObject) {
        try {
            Log.e("SavedAddress", obj.toString())
            binding.rootView.visibility = View.VISIBLE
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataObject = obj.getJSONObject("data")

                        if (dataObject.has("shopId") && !dataObject.isNull("shopId")) {
                            val id = dataObject.getString("shopId")
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_SHOP_ID,
                                id
                            )
                        }
                        launchPaymentActivity()
                    }
                }
            } else {
                binding.lvProducts.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkLocationPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION
                )
            } else {
                getLastLocation()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initLocationReference() {
        try {
            mSettingsClient = LocationServices.getSettingsClient(this)
            mLocationRequest = LocationRequest()
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            mLocationRequest.setInterval(0)
            mLocationRequest.setFastestInterval(0)
            mLocationRequest.setNumUpdates(1)
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)
            builder.setNeedBle(true)
            mLocationSettingsRequest = builder.build()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out kotlin.String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        val addressDto = CommonClass.getCurrentAddress(
                            context,
                            latitude, longitude
                        )
                        var text = ""
                        if (addressDto != null) {
                            var address1 = ""
                            if (addressDto.houseNo.trim().isNotEmpty()
                            ) {
                                address1 = addressDto.houseNo + ", "
                            }
                            if (addressDto.subHouseNo.trim().isNotEmpty()
                            ) {
                                address1 = address1 + addressDto.subHouseNo + ", "
                            }
                            text = text + address1
                            if (addressDto.area.trim().isNotEmpty()
                            ) {
                                text = text + addressDto.area + ", "
                            }
                            if (addressDto.city.trim().isNotEmpty()
                            ) {
                                text = text + addressDto.city + ", "
                            }
                            if (addressDto.state.trim().isNotEmpty()
                            ) {
                                text = text + addressDto.state + ", "
                            }
                            if (addressDto.country.trim().isNotEmpty()
                            ) {
                                text = text + addressDto.country + ", "
                            }
                            text = text.replace(", $".toRegex(), "")
                        } else {
                            text = getString(R.string.location_selected)
                        }
                        binding.tvAddress.visibility = View.VISIBLE
                        binding.tvAddress.text = text
                    }
                }
            } else {
                mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                    .addOnFailureListener(this) { e ->
                        when ((e as ApiException).statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                                val rae = e as ResolvableApiException
                                val intentSenderRequest =
                                    IntentSenderRequest.Builder(rae.resolution.intentSender).build()
                                locationIntentResultLauncher.launch(intentSenderRequest)
                            } catch (sie: SendIntentException) {
                            }
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                val errorMessage =
                                    "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings."
                                CommonClass.showToastMessage(
                                    this,
                                    binding.rootView,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                )
                            }
                        }
                    }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val locationIntentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result != null) {
                getLastLocation()
            }
        }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun launchAddNewAddressActivity(addressListDto: AddressListDto?) {
        try {
            val intent = Intent(context, AddressAddActivity::class.java)
            intent.putExtra("addressListDto", addressListDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            resultLauncher.launch(intent)
        } catch (e: Exception) {

        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                addressItemList.clear()
                getSavedAddress()
            }
        }


    private fun launchPaymentActivity() {
        try {
            val intent = Intent(context, PaymentSelectionActivity::class.java)
            intent.putExtra("totalCost", total)
            intent.putExtra("productCost", totalAmount)
            intent.putExtra("packageCost", packingCharges)
            intent.putExtra("deliveryCost", deliveryCharge)
            intent.putExtra("reductionAmount", 0F)
            intent.putExtra("cart", cartItemArray.toString())
            if (selectedAddressListDto != null) {
                intent.putExtra("deliveryAddressId", selectedAddressListDto!!._id)
                intent.putExtra("lat", selectedAddressListDto!!.lat)
                intent.putExtra("lng", selectedAddressListDto!!.lng)
            }
            intent.putExtra("isCurrentLocation", isCurrLoc)
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