package com.illagu.attil.address

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.illagu.attil.SubModuleActivity
import com.illagu.attil.address.dto.AddressListDto
import com.illagu.attil.databinding.ActivityAddressAddBinding
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.AppUtils
import com.illagu.attil.utils.CommonClass
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
import kotlin.lazy


class AddressAddActivity : SubModuleActivity() {

    private val context: Context = this@AddressAddActivity
    private val binding by lazy { ActivityAddressAddBinding.inflate(layoutInflater) }

    private lateinit var addressViewModel: AddressViewModel

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest

    private var latitude = "0"
    private var longitude = "0"
    private var addressId = ""
    private var submitFlag = false

    private lateinit var addressListDto: AddressListDto
    private val REQUEST_FINE_LOCATION = 104

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
        checkLocationPermission()
        handleUiElement()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title =
                resources.getString(R.string.address)
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
            addressListDto = intent.getSerializableExtra("addressListDto") as AddressListDto
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
            if (addressListDto != null) {
                binding.btnAddAddress.text = getString(R.string.update_address)
                addressId = addressListDto._id
                binding.etFullName.setText(addressListDto.fullName)
                binding.etMobileNo.setText(addressListDto.phoneNumber)
                binding.etAdditionalMobileNo.setText(addressListDto.additionalPhoneNumber)
                binding.etPpinCode.setText(addressListDto.pinCode)
                binding.etFlatNo.setText(addressListDto.doorNo)
                binding.etArea.setText(addressListDto.street)
                binding.etLandmark.setText(addressListDto.landmark)
                binding.etCity.setText(addressListDto.townORcity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.btnAddAddress.setOnClickListener {

                if (!submitFlag) {
                    submitFlag = true
                    saveAddress()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validate(): Boolean {
        var flag = true
        try {
            if (binding.etMobileNo.text.toString().isEmpty()) {
                binding.etMobileNo.requestFocus()
                binding.etMobileNo.error = resources.getString(R.string.required)
                flag = false
            }/* else if (binding.etPassword.text.toString().isEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = resources.getString(R.string.required)
                flag = false
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        submitFlag = flag
        return flag
    }

    private fun saveAddress() {
        try {
            if (validate()) {
                if (AppUtils.isConnectedToInternet(context)) {
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "userId",
                        preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                    )
                    requestObject.addProperty("doorNo", binding.etFlatNo.text.toString())
                    requestObject.addProperty("street", binding.etArea.text.toString())
                    requestObject.addProperty("townORcity", binding.etCity.text.toString())
                    requestObject.addProperty("pinCode", binding.etPpinCode.text.toString())
                    requestObject.addProperty("landmark", binding.etLandmark.text.toString())
                    requestObject.addProperty("phoneNumber", binding.etMobileNo.text.toString())
                    requestObject.addProperty("isCurrentLocation", false)
                    requestObject.addProperty("fullName", binding.etFullName.text.toString())
                    requestObject.addProperty(
                        "additionalPhoneNumber",
                        binding.etAdditionalMobileNo.text.toString()
                    )
                    requestObject.addProperty(
                        "addressId",
                        addressId
                    )
                    showProgressBar()

                    addressViewModel.saveAddress(
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseSavedAddressResponse(obj: JSONObject) {
        try {
            submitFlag = false
            Log.e("SavedAddress", obj.toString())
            binding.rootView.visibility = View.VISIBLE
            if (obj.has("code") && obj.getInt("code") == 200) {
                handleResponse()
            } else {
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleResponse() {
        try {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
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
                        if (addressDto != null) {
                            if (addressDto.state.trim().isNotEmpty()
                            ) {
                                binding.etState.setText(addressDto.state)
                            }
                            if (addressDto.country.trim().isNotEmpty()
                            ) {
                                binding.etCountry.setText(addressDto.country)
                            }
                        }
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
}