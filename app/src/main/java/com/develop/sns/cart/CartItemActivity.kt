package com.develop.sns.cart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.cart.adapter.CartItemListAdapter
import com.develop.sns.cart.dto.CartDetailsDto
import com.develop.sns.cart.dto.CartItemDto
import com.develop.sns.cart.listener.CartListener
import com.develop.sns.databinding.ActivityCartItemBinding
import com.develop.sns.home.details.adapter.ItemDetailsListAdapter
import com.develop.sns.home.offers.dto.NormalOfferPriceDto
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.FieldPosition

class CartItemActivity : SubModuleActivity(), CartListener {

    private val context: Context = this@CartItemActivity
    private val binding by lazy { ActivityCartItemBinding.inflate(layoutInflater) }

    private lateinit var cartItemList: ArrayList<CartItemDto>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var cartItemListAdapter: CartItemListAdapter
    private lateinit var cartMap: HashMap<String, CartDetailsDto>

    var packageType = ""
    var offerType = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        getIntentValue()
        initClassReference()
        handleUiElement()
        getCartItems()
    }

    private fun initToolBar(size: Int) {
        try {
            binding.lnToolbar.toolbar.title =
                resources.getString(R.string.total_cart) + " - " + size
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            cartItemList = ArrayList()
            cartMap = java.util.HashMap()
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

    private fun getCartItems() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty(
                    "userId",
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                )
                showProgressBar()
                val cartViewModel = CartViewModel()
                cartViewModel.getCartItem(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this, { jsonObject ->
                    dismissProgressBar()
                    parseCartItemsResponse(jsonObject)
                })
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

    private fun parseCartItemsResponse(obj: JSONObject) {
        try {
            Log.e("CartItem", obj.toString())
            binding.rootView.visibility = View.VISIBLE
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val itemObject = dataArray.getJSONObject(i)
                            val cartItemDto = CartItemDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                cartItemDto.id = itemObject.getString("_id")
                            }

                            if (itemObject.has("productDetails") && !itemObject.isNull("productDetails")) {
                                val productDetailsObj = itemObject.getJSONObject("productDetails")

                                if (productDetailsObj.has("_id")
                                    && !productDetailsObj.isNull("_id")
                                ) {
                                    cartItemDto.productId = productDetailsObj.getString("_id")
                                }

                                if (productDetailsObj.has("productCode") && !productDetailsObj.isNull(
                                        "productCode"
                                    )
                                ) {
                                    cartItemDto.productCode =
                                        productDetailsObj.getString("productCode")
                                }

                                if (productDetailsObj.has("productName") && !productDetailsObj.isNull(
                                        "productName"
                                    )
                                ) {
                                    cartItemDto.productName =
                                        productDetailsObj.getString("productName")
                                }

                                val brandImageList = ArrayList<String>()
                                if (productDetailsObj.has("brandImage") && !productDetailsObj.isNull(
                                        "brandImage"
                                    )
                                ) {
                                    val brandImageArray =
                                        productDetailsObj.getJSONArray("brandImage")
                                    for (j in 0 until brandImageArray.length()) {
                                        brandImageList.add(brandImageArray.getString(j))
                                    }
                                }
                                cartItemDto.brandImage = brandImageList

                                if (productDetailsObj.has("brandId") && !productDetailsObj.isNull("brandId")) {
                                    cartItemDto.brandId = productDetailsObj.getString("brandId")
                                }

                                if (productDetailsObj.has("brandName") && !productDetailsObj.isNull(
                                        "brandName"
                                    )
                                ) {
                                    cartItemDto.brandName = productDetailsObj.getString("brandName")
                                }

                                if (productDetailsObj.has("packageType") && !productDetailsObj.isNull(
                                        "packageType"
                                    )
                                ) {
                                    cartItemDto.packageType =
                                        productDetailsObj.getString("packageType")
                                    packageType = productDetailsObj.getString("packageType")
                                }

                                if (productDetailsObj.has("offerType")
                                    && !productDetailsObj.isNull("offerType")
                                ) {
                                    cartItemDto.offerType = productDetailsObj.getString("offerType")
                                    offerType = productDetailsObj.getString("offerType")
                                }

                                /* if (productDetailsObj.has("description")
                                     && !productDetailsObj.isNull("description")) {
                                     cartItemDto.description =
                                         productDetailsObj.getString("description")
                                 }

                                 if (productDetailsObj.has("createdAtTZ")
                                     && !productDetailsObj.isNull("createdAtTZ")) {
                                     cartItemDto.createdAtTZ =
                                         productDetailsObj.getString("createdAtTZ")
                                 }

                                 val priceDetailsArray = ArrayList<NormalOfferPriceDto>()
                                 if (productDetailsObj.has("priceDetails")
                                     && !productDetailsObj.isNull("priceDetails")) {
                                     val priceArray = productDetailsObj.getJSONArray("priceDetails")
                                     val sortedPriceArray = sortJsonArray(priceArray)
                                     for (k in 0 until sortedPriceArray.length()) {
                                         val priceObject = sortedPriceArray.getJSONObject(k)
                                         val normalOfferPriceDto = NormalOfferPriceDto()

                                         normalOfferPriceDto.packageType = packageType

                                         normalOfferPriceDto.offerType = offerType

                                         if (priceObject.has("_id") && !priceObject.isNull("_id")) {
                                             normalOfferPriceDto.id = priceObject.getString("_id")
                                         }

                                         if (priceObject.has("measureType") && !priceObject.isNull("measureType")) {
                                             normalOfferPriceDto.measureType =
                                                 priceObject.getString("measureType")
                                         }

                                         if (priceObject.has("normalPrice") && !priceObject.isNull("normalPrice")) {
                                             normalOfferPriceDto.normalPrice =
                                                 priceObject.getInt("normalPrice")
                                         }

                                         if (priceObject.has("attilPrice") && !priceObject.isNull("attilPrice")) {
                                             normalOfferPriceDto.attilPrice =
                                                 priceObject.getInt("attilPrice")
                                         }

                                         if (priceObject.has("availability") && !priceObject.isNull("availability")) {
                                             normalOfferPriceDto.availability =
                                                 priceObject.getInt("availability")
                                         }

                                         if (priceObject.has("unit") && !priceObject.isNull("unit")) {
                                             normalOfferPriceDto.unit =
                                                 priceObject.getInt("unit")
                                         }

                                         if (priceObject.has("minUnit") && !priceObject.isNull("minUnit")) {
                                             val minUnitObject = priceObject.getJSONObject("minUnit")

                                             if (minUnitObject.has("measureType")) {
                                                 normalOfferPriceDto.minUnitMeasureType =
                                                     minUnitObject.getString("measureType")
                                             }

                                             if (minUnitObject.has("unit") && !minUnitObject.isNull("unit")) {
                                                 normalOfferPriceDto.minUnit =
                                                     minUnitObject.getInt("unit")
                                             }
                                         }

                                         if (priceObject.has("maxUnit") && !priceObject.isNull("maxUnit")) {

                                             val maxUnitObject = priceObject.getJSONObject("maxUnit")

                                             if (maxUnitObject.has("measureType")) {
                                                 normalOfferPriceDto.maxUnitMeasureType =
                                                     maxUnitObject.getString("measureType")
                                             }
                                             if (maxUnitObject.has("unit") && !maxUnitObject.isNull("unit")) {
                                                 normalOfferPriceDto.maxUnit =
                                                     maxUnitObject.getInt("unit")
                                             }
                                         }

                                         if (priceObject.has("offerDetails") && !priceObject.isNull("offerDetails")) {
                                             val offerDetailsObject =
                                                 priceObject.getJSONObject("offerDetails")
                                             if (offerDetailsObject.has("measureType")) {
                                                 normalOfferPriceDto.offerMeasureType =
                                                     offerDetailsObject.getString("measureType")
                                             }

                                             if (offerDetailsObject.has("unit") && !offerDetailsObject.isNull(
                                                     "unit"
                                                 )
                                             ) {
                                                 normalOfferPriceDto.offerUnit =
                                                     offerDetailsObject.getInt("unit")
                                             }

                                             if (offerDetailsObject.has("offerPercentage")
                                                 && !offerDetailsObject.isNull(
                                                     "offerPercentage"
                                                 )
                                             ) {
                                                 normalOfferPriceDto.offerPercentage =
                                                     offerDetailsObject.getInt("offerPercentage")
                                             }

                                             if (offerDetailsObject.has("extraProduct")
                                                 && !offerDetailsObject.isNull("extraProduct")
                                             ) {
                                                 val extraProdObj =
                                                     offerDetailsObject.getJSONObject("extraProduct")
                                                 if (extraProdObj.has("productName")
                                                     && !extraProdObj.isNull("productName")
                                                 ) {
                                                     normalOfferPriceDto.bogeProductName =
                                                         extraProdObj.getString("productName")
                                                 }
                                                 if (extraProdObj.has("brandImage")
                                                     && !extraProdObj.isNull("brandImage")
                                                 ) {
                                                     val extraBrandImg =
                                                         extraProdObj.getJSONArray("brandImage")
                                                     normalOfferPriceDto.bogeProductImg =
                                                         extraBrandImg.getString(0)
                                                 }
                                                 if (extraProdObj.has("priceDetails")
                                                     && !extraProdObj.isNull("priceDetails")
                                                 ) {
                                                     val extraPrice =
                                                         extraProdObj.getJSONArray("priceDetails")
                                                     val extraPriceObj = extraPrice.getJSONObject(0)
                                                     if (extraPriceObj.has("unit") && !extraPriceObj.isNull(
                                                             "unit"
                                                         )
                                                     ) {
                                                         normalOfferPriceDto.bogeUnit =
                                                             extraPriceObj.getInt("unit")
                                                     }
                                                     if (extraPriceObj.has("measureType") && !extraPriceObj.isNull(
                                                             "measureType"
                                                         )
                                                     ) {
                                                         normalOfferPriceDto.bogeMeasureType =
                                                             extraPriceObj.getString("measureType")
                                                     }
                                                 }
                                             }
                                         }

                                         priceDetailsArray.add(normalOfferPriceDto)
                                     }
                                 }
                                 cartItemDto.priceDetails = priceDetailsArray*/
                            }

                            val cartDetailsList = ArrayList<CartDetailsDto>()
                            if (itemObject.has("cart") && !itemObject.isNull("cart")) {
                                val cartArray = itemObject.getJSONArray("cart")
                                for (k in 0 until cartArray.length()) {
                                    val cartObject = cartArray.getJSONObject(k)
                                    val cartDetailsDto = CartDetailsDto()

                                    cartDetailsDto.packageType = packageType

                                    cartDetailsDto.offerType = offerType

                                    if (cartObject.has("_id") && !cartObject.isNull("_id")) {
                                        cartDetailsDto.cartItemId = cartObject.getString("_id")
                                    }

                                    if (cartObject.has("availability") && !cartObject.isNull("availability")) {
                                        cartDetailsDto.availability =
                                            cartObject.getInt("availability")
                                    }

                                    if (cartObject.has("measureType") && !cartObject.isNull("measureType")) {
                                        cartDetailsDto.measureType =
                                            cartObject.getString("measureType")
                                    }

                                    if (cartObject.has("unit") && !cartObject.isNull("unit")) {
                                        cartDetailsDto.unit = cartObject.getInt("unit")
                                    }

                                    if (cartObject.has("normalPrice") && !cartObject.isNull("normalPrice")) {
                                        cartDetailsDto.normalPrice =
                                            cartObject.getInt("normalPrice")
                                    }

                                    if (cartObject.has("attilPrice") && !cartObject.isNull("attilPrice")) {
                                        cartDetailsDto.attilPrice = cartObject.getInt("attilPrice")
                                    }

                                    if (cartObject.has("createdAtTZ") && !cartObject.isNull("createdAtTZ")) {
                                        cartDetailsDto.updatedAt =
                                            CommonClass.getDateTimeFromUtc(
                                                cartObject.getString("createdAtTZ"),
                                                AppConstant.appDateFormat,
                                                AppConstant.appDateTimeFormat_TimeZone
                                            )
                                    }

                                    if (cartObject.has("updatedAtTZ") && !cartObject.isNull("updatedAtTZ")) {
                                        cartDetailsDto.updatedAt =
                                            CommonClass.getDateTimeFromUtc(
                                                cartObject.getString("updatedAtTZ"),
                                                AppConstant.appDateFormat,
                                                AppConstant.appDateTimeFormat_TimeZone
                                            )
                                    }

                                    if (cartObject.has("selectedMin") && !cartObject.isNull("selectedMin")) {
                                        val minUnitObject = cartObject.getJSONObject("selectedMin")

                                        if (minUnitObject.has("measureType")) {
                                            cartDetailsDto.cartMinUnitMeasureType =
                                                minUnitObject.getString("measureType")
                                        }

                                        if (minUnitObject.has("unit") && !minUnitObject.isNull("unit")) {
                                            cartDetailsDto.cartSelectedMinUnit =
                                                minUnitObject.getInt("unit")
                                        }
                                    }

                                    if (cartObject.has("selectedMax") && !cartObject.isNull("selectedMax")) {

                                        val maxUnitObject = cartObject.getJSONObject("selectedMax")

                                        if (maxUnitObject.has("measureType")) {
                                            cartDetailsDto.cartMaxUnitMeasureType =
                                                maxUnitObject.getString("measureType")
                                        }
                                        if (maxUnitObject.has("unit") && !maxUnitObject.isNull("unit")) {
                                            cartDetailsDto.cartSelectedMaxUnit =
                                                maxUnitObject.getInt("unit")
                                        }
                                    }

                                    if (cartObject.has("minUnit") && !cartObject.isNull("minUnit")) {
                                        val minUnitObject = cartObject.getJSONObject("minUnit")

                                        if (minUnitObject.has("measureType")) {
                                            cartDetailsDto.minUnitMeasureType =
                                                minUnitObject.getString("measureType")
                                        }

                                        if (minUnitObject.has("unit") && !minUnitObject.isNull("unit")) {
                                            cartDetailsDto.minUnit =
                                                minUnitObject.getInt("unit")
                                        }
                                    }

                                    if (cartObject.has("maxUnit") && !cartObject.isNull("maxUnit")) {

                                        val maxUnitObject = cartObject.getJSONObject("maxUnit")

                                        if (maxUnitObject.has("measureType")) {
                                            cartDetailsDto.maxUnitMeasureType =
                                                maxUnitObject.getString("measureType")
                                        }
                                        if (maxUnitObject.has("unit") && !maxUnitObject.isNull("unit")) {
                                            cartDetailsDto.maxUnit =
                                                maxUnitObject.getInt("unit")
                                        }
                                    }

                                    if (cartObject.has("selectedItemCount") && !cartObject.isNull("selectedItemCount")) {
                                        cartDetailsDto.cartSelectedItemCount =
                                            cartObject.getInt("selectedItemCount")
                                    }

                                    if (cartObject.has("offerDetails") && !cartObject.isNull("offerDetails")) {
                                        val offerDetailsObject =
                                            cartObject.getJSONObject("offerDetails")
                                        if (offerDetailsObject.has("measureType")) {
                                            cartDetailsDto.offerMeasureType =
                                                offerDetailsObject.getString("measureType")
                                        }

                                        if (offerDetailsObject.has("unit") && !offerDetailsObject.isNull(
                                                "unit"
                                            )
                                        ) {
                                            cartDetailsDto.offerUnit =
                                                offerDetailsObject.getInt("unit")
                                        }

                                        if (offerDetailsObject.has("offerPercentage")
                                            && !offerDetailsObject.isNull(
                                                "offerPercentage"
                                            )
                                        ) {
                                            cartDetailsDto.offerPercentage =
                                                offerDetailsObject.getInt("offerPercentage")
                                        }

                                        if (offerDetailsObject.has("extraProduct")
                                            && !offerDetailsObject.isNull("extraProduct")
                                        ) {
                                            val extraProdObj =
                                                offerDetailsObject.getJSONObject("extraProduct")
                                            if (extraProdObj.has("productName")
                                                && !extraProdObj.isNull("productName")
                                            ) {
                                                cartDetailsDto.bogeProductName =
                                                    extraProdObj.getString("productName")
                                            }
                                            if (extraProdObj.has("brandImage")
                                                && !extraProdObj.isNull("brandImage")
                                            ) {
                                                val extraBrandImg =
                                                    extraProdObj.getJSONArray("brandImage")
                                                cartDetailsDto.bogeProductImg =
                                                    extraBrandImg.getString(0)
                                            }
                                            if (extraProdObj.has("priceDetails")
                                                && !extraProdObj.isNull("priceDetails")
                                            ) {
                                                val extraPrice =
                                                    extraProdObj.getJSONArray("priceDetails")
                                                val extraPriceObj = extraPrice.getJSONObject(0)
                                                if (extraPriceObj.has("unit") && !extraPriceObj.isNull(
                                                        "unit"
                                                    )
                                                ) {
                                                    cartDetailsDto.bogeUnit =
                                                        extraPriceObj.getInt("unit")
                                                }
                                                if (extraPriceObj.has("measureType") && !extraPriceObj.isNull(
                                                        "measureType"
                                                    )
                                                ) {
                                                    cartDetailsDto.bogeMeasureType =
                                                        extraPriceObj.getString("measureType")
                                                }
                                            }
                                        }
                                    }

                                    cartDetailsList.add(cartDetailsDto)
                                }
                            }
                            cartItemDto.cartDetails = cartDetailsList

                            cartItemList.add(cartItemDto)
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
            initToolBar(cartItemList.size)
            if (!cartItemList.isNullOrEmpty()) {
                binding.lvProducts.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                linearLayoutManager = LinearLayoutManager(context)
                binding.lvProducts.layoutManager = linearLayoutManager
                cartItemListAdapter =
                    CartItemListAdapter(context, cartItemList, this@CartItemActivity)
                binding.lvProducts.adapter = cartItemListAdapter
            } else {
                binding.lvProducts.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            }
            calculateTotal(cartItemList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateTotal(cartItemList: java.util.ArrayList<CartItemDto>) {
        try {
            var totalAmount = 0F
            for (k in 0 until cartItemList.size) {
                val cartItemDto = cartItemList[k]
                val cartDetailsList = cartItemDto.cartDetails
                for (i in 0 until cartDetailsList.size) {
                    val cartDetailsDto = cartDetailsList[i]
                    if (cartItemDto.packageType == "loose" && cartItemDto.offerType == "normal") {
                        val qty =
                            cartDetailsDto.cartSelectedMinUnit + (cartDetailsDto.cartSelectedMaxUnit * 1000)

                        val result = qty.toFloat() / cartDetailsDto.unit.toFloat()
                        totalAmount += result.times(cartDetailsDto.attilPrice)
                    } else if ((cartItemDto.packageType == "packed" && cartItemDto.offerType == "normal")
                        || (cartItemDto.packageType == "packed" && cartItemDto.offerType == "BOGO")
                        || (cartItemDto.packageType == "packed" && cartItemDto.offerType == "BOGE")
                    ) {
                        val qty = cartDetailsDto.cartSelectedItemCount
                        totalAmount += qty.times(cartDetailsDto.attilPrice)
                    }
                }
            }
            updateTotal(totalAmount)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateTotal(totalPrice: Float) {
        binding.tvTotalPrice.text =
            getString(R.string.Rs).plus(" ").plus("%.2f".format(totalPrice))
    }

    override fun selectItem(cartItemDto: CartItemDto) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun handleItem(
        cartDetailsDto: CartDetailsDto,
        isAdd: Boolean,
        itemGroupPosition: Int,
        position: Int
    ) {
        try {
            if (isAdd) {
                //Add Item From cart
                Log.e("TAG", "Add Item")
                addItem(cartDetailsDto, itemGroupPosition, position)
            } else {
                //Remove Item From cart
                Log.e("TAG", "Remove Item")
                removeItem(cartDetailsDto, itemGroupPosition, position)
                addItem(cartDetailsDto, itemGroupPosition, position)
            }
            cartItemListAdapter.notifyItemChanged(itemGroupPosition)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun remove(itemGroupPosition: Int, position: Int) {
        try {
            val cartDetailsDto = cartItemList[itemGroupPosition].cartDetails[position]
            removeCartItem(cartDetailsDto.cartItemId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addItem(
        cartDetailsDto: CartDetailsDto,
        itemGroupPosition: Int,
        position: Int
    ) {
        try {
            cartItemList[itemGroupPosition].cartDetails[position] = cartDetailsDto
            calculateTotal(cartItemList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeItem(
        cartDetailsDto: CartDetailsDto,
        itemGroupPosition: Int,
        position: Int
    ) {
        try {
            cartItemList[itemGroupPosition].cartDetails[position] = cartDetailsDto
            calculateTotal(cartItemList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeCartItem(cartItemId: String) {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty(
                    "userId",
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                )
                requestObject.addProperty(
                    "cartId",
                    cartItemId
                )
                showProgressBar()
                val cartViewModel = CartViewModel()
                cartViewModel.removeCartItem(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this, { jsonObject ->
                    dismissProgressBar()
                    parseRemoveCartItemResponse(jsonObject)
                })
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

    private fun parseRemoveCartItemResponse(obj: JSONObject) {
        try {
            Log.e("removeCartItem", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {

                }
            } else {
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}