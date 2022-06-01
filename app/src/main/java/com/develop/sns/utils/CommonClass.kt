package com.develop.sns.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Typeface
import android.location.Geocoder
import android.os.Build
import android.location.Address
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.develop.sns.MainActivity
import com.develop.sns.R
import com.develop.sns.address.dto.AddressDto
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class CommonClass {
    private val context: Context? = null

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    companion object {
        fun getFileDataFromFilePath(resumeAttachmentPath: String?): ByteArray? {
            val tempFile = File(resumeAttachmentPath)
            var byteArray: ByteArray? = null
            try {
                val inputStream: InputStream = FileInputStream(tempFile)
                val bos = ByteArrayOutputStream()
                val b = ByteArray(1024 * 8)
                var bytesRead = 0
                while (inputStream.read(b).also { bytesRead = it } != -1) {
                    bos.write(b, 0, bytesRead)
                }
                byteArray = bos.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return byteArray
        }

        fun showToastMessage(context: Context, rootView: View?, message: String?, length: Int) {
            try {
                val snackbar = Snackbar.make(rootView!!, message!!, length)
                val snackBarView: View = snackbar.view
                snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red_A700
                    )
                )
                val textView: TextView =
                    snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
                textView.maxLines = Int.MAX_VALUE
                textView.ellipsize = null
                textView.setTextColor(ContextCompat.getColor(context, R.color.white))
                textView.textSize = 14f
                textView.setTypeface(textView.typeface, Typeface.BOLD)
                textView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        snackbar.dismiss()
                    }
                })
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                } else {
                    textView.gravity = Gravity.CENTER_HORIZONTAL
                }
                snackbar.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //Password Validation
        fun validatePassword(password: String): Boolean {
            var flag = false
            try {
                val inputString: CharSequence = password
                val pattern: Pattern =
                    Pattern.compile(AppConstant.ALPHA_NUMERIC_PATTERN, Pattern.CASE_INSENSITIVE)
                val matcher: Matcher = pattern.matcher(inputString)
                flag = matcher.matches()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return flag
        }

        fun logoutSession(context: Context) {
            try {
                val preferenceHelper = PreferenceHelper(context)
                //Toast.makeText(context, "Session expired, because you Signed on another device.", Toast.LENGTH_SHORT).show();
                val languageId = preferenceHelper.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
                val firstTime = preferenceHelper.getIntFromSharedPrefs(AppConstant.KEY_FIRST_TIME)
                preferenceHelper.clear()
                preferenceHelper.saveIntValueToSharedPrefs(AppConstant.KEY_LANGUAGE_ID, languageId)
                preferenceHelper.saveIntValueToSharedPrefs(AppConstant.KEY_FIRST_TIME, firstTime)
                ApplicationManager.instance.setUpPhoneLanguage()

                val i = Intent(context, MainActivity::class.java)
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                // Add new Flag to start new Activity
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                // Staring Login Activity
                context.startActivity(i)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun getScreenWidth(activity: Activity): Int {
            var width = 0
            try {
                val display = activity.windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                width = size.x
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return width
        }

        fun handleErrorResponse(context: Context, jsonObject: JSONObject, view: View) {
            val statusCode = jsonObject.getInt("statusCode")
            if (statusCode == 401) {
                logoutSession(context)
                (context as Activity).finish()
            } else {
                showToastMessage(
                    context,
                    view,
                    jsonObject.getString("message"),
                    Toast.LENGTH_SHORT
                )
            }
        }

        fun getDateTimeFromUtc(
            str: String,
            outputFormat: SimpleDateFormat,
            inputFormat: SimpleDateFormat
        ): String {
            var dateTime = ""
            try {
                val tz = TimeZone.getTimeZone("UTC")
                inputFormat.timeZone = tz
                val date = inputFormat.parse(str)

                val tzInAmerica = TimeZone.getDefault()
                outputFormat.timeZone = tzInAmerica
                dateTime = outputFormat.format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return dateTime
        }

        fun getCurrentAddress(
            context: Context,
            latitude: String?,
            longitude: String?
        ): AddressDto? {
            var addressDto: AddressDto? = null
            try {
                if (latitude != null && latitude.trim { it <= ' ' }.length > 0 && longitude != null && longitude.trim { it <= ' ' }.length > 0) {
                    addressDto = AddressDto()
                    val addresses: List<Address>?
                    val geocoder = Geocoder(context, Locale.ENGLISH)
                    addresses =
                        geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        for (j in addresses.indices) {
                            val locationAddress: Address = addresses[j]
                            //Log.i("ssssss0", "" + locationAddress.getAddressLine(0));
//                        Log.i("ssssss1", "" + locationAddress.getSubLocality());
//                        Log.i("ssssss2", "" + locationAddress.getSubAdminArea());
                        }
                        val locationAddress: Address = addresses[0]
                        val sb = StringBuilder()
                        for (i in 0 until locationAddress.maxAddressLineIndex) {
//                        Log.i("ssssss" + i, "" + locationAddress.getAddressLine(i));
                            sb.append(locationAddress.getAddressLine(i)).append(", ")
                        }
                        val address: String = locationAddress.getAddressLine(0)

                        val city: String = locationAddress.locality
                        val state: String = locationAddress.adminArea
                        val country: String = locationAddress.countryName
                        val postalCode: String = locationAddress.postalCode
                        val countryCode: String = locationAddress.countryCode
                        val houseNo: String? = locationAddress.subThoroughfare
                        val subHouseNo: String? = locationAddress.thoroughfare
                        val area: String = locationAddress.subLocality


                        Log.e("getSubAdminArea", "" + locationAddress.subAdminArea)
                        Log.e("getSubLocality", "" + locationAddress.subLocality)
                        Log.e("getLocality", "" + locationAddress.locality)
                        Log.e("getAdminArea", "" + locationAddress.adminArea)
                        Log.e("getCountryName", "" + locationAddress.countryName)
                        Log.e("getFeatureName", "" + locationAddress.featureName)
                        Log.e("getPostalCode", "" + locationAddress.postalCode)
                        Log.e("getThoroughfare", "" + locationAddress.thoroughfare)
                        Log.e("getSubThoroughfare", "" + locationAddress.subThoroughfare)

                        addressDto.city = city
                        addressDto.state = state
                        addressDto.country = country
                        addressDto.pinCode = postalCode
                        addressDto.addressLine2 = address
                        var address1 = ""
                        if (houseNo != null) {
                            if (houseNo.trim { it <= ' ' }.isNotEmpty()) {
                                address1 = "$houseNo, "
                            }
                        }
                        if (subHouseNo != null) {
                            if (subHouseNo.trim { it <= ' ' }.isNotEmpty()) {
                                address1 = "$address1$subHouseNo, "
                            }
                        }
                        if (area.trim { it <= ' ' }.isNotEmpty()) {
                            address1 = "$address1$area, "
                        }
                        address1 = address1.replace(", $".toRegex(), "")
                        addressDto.addressLine1 = address1
                        addressDto.countryCode = countryCode
                        if (houseNo != null) {
                            addressDto.houseNo = houseNo
                        }
                        if (subHouseNo != null) {
                            addressDto.subHouseNo = subHouseNo
                        }
                        addressDto.area = area
                        addressDto.latitude = latitude
                        addressDto.longitude = longitude
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return addressDto
        }

    }
}