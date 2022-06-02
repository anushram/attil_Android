package com.develop.sns.utils

import android.os.Environment
import java.text.SimpleDateFormat
import java.util.*

object AppConstant {

    //Preference helper key
    const val KEY_FIRST_TIME = "firstTime"
    const val KEY_TOKEN = "token"
    const val KEY_LANGUAGE = "LANG"
    const val KEY_LANGUAGE_ID = "languageId"
    const val KEY_USER_ID = "userId"
    const val KEY_NAME = "displayName"
    const val KEY_MOBILE_NO = "mobileNo"
    const val KEY_SECRET = "secretKey"
    const val KEY_GCM_ID = "gcmId"
    const val KEY_DEVICE_ID = "deviceId"
    const val KEY_SYSTEM_LANGUAGES = "systemLanguages"
    const val KEY_MIN_UNITS = "minUnits"
    const val KEY_MAX_UNITS = "maxUnits"
    const val KEY_IS_PAYMENT_ACTIVE = "isPaymentActive"
    const val KEY_IS_COD_ACTIVE = "isCODActive"
    const val KEY_PACKAGE_COST = "packageCost"
    const val KEY_DELIVERY_COST = "deliveryCost"
    const val KEY_MIN_FREE_DELIVERY = "minimumFreeDelivery"
    const val KEY_MIN_IOS = "minIOSVersion"
    const val KEY_MIN_ANDROID = "minAndroidVersion"
    const val KEY_UPDATED_AT = "updatedAt"
    const val KEY_UPDATED_AT_TZ = "updatedAtTZ"
    const val KEY_OTP_ID = "otpId"
    const val KEY_CART_ITEM = "cartItem"
    const val KEY_PRODUCTS_OBJ = "productObj"
    const val KEY_USER_NAME = "userName"
    const val KEY_USER_PWD = "userPwd"
    const val KEY_ADDRESS_MAIN_ID = "addressMainId"
    const val KEY_CURRENT_LATITUDE = "currentLatitude"
    const val KEY_CURRENT_LONGITUDE = "currentLongitude"
    const val KEY_SHOP_ID = "shopID"

    //LanguageId
    var LANGUAGE_ID = 1
    var LANGUAGE = "en"

    //Language staticId
    const val LANGUAGE_TYPE_TAMIL = 2
    const val LANGUAGE_TYPE_ENGLISH = 1

    //For Attachments
    val STORAGE = Environment.getExternalStorageDirectory()
    const val IMAGE_DIR = "Attil"
    const val TEMP_IMAGE = "attil_temp.jpg"

    //Field Validation
    const val EMAIL_PATTERN = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
    const val ALPHA_NUMERIC_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!$%@#£€*?&]{4,}$"

    const val OFFERS_FRAGMENT = 0
    const val PRODUCTS_FRAGMENT = 1
    const val ORDERS_FRAGMENT = 2
    const val PROFILE_FRAGMENT = 3

    const val PRODUCT_FRAGMENT = 1
    const val PACKED_FRAGMENT = 2

    //Time Format
    val appFormatTimeStart = SimpleDateFormat("hh:mm", Locale.getDefault())
    val appFormatTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val serverFormatTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

    //Date format
    val appDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val yearOnly = SimpleDateFormat("yyyy", Locale.getDefault())
    val monthOnly = SimpleDateFormat("MM", Locale.getDefault())
    val dayOnly = SimpleDateFormat("dd", Locale.getDefault())
    val serverDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val view_date_month = SimpleDateFormat("dd-MMM", Locale.getDefault())
    val view_day = SimpleDateFormat("EEE", Locale.ENGLISH)

    //Date Time Format
    val appDateTimeFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())
    val appDateTimeSSFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.getDefault())
    val appDateTimeFormat_no_yyyy = SimpleDateFormat("dd-MMM hh:mm a", Locale.getDefault())
    val appDateTimeFormat_no_yyyy_extra = SimpleDateFormat("dd-MMM  hh:mm a", Locale.getDefault())
    val appDateTimeFormat_TimeZone = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val serverDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
}