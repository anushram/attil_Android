package com.develop.sns.cart

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
import com.develop.sns.address.AddressSelectionActivity
import com.develop.sns.cart.adapter.CartItemListAdapter
import com.develop.sns.cart.dto.CartListDto
import com.develop.sns.cart.listener.CartListener
import com.develop.sns.databinding.ActivityCartItemBinding
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

class CartItemActivity : SubModuleActivity(), CartListener {

    private val context: Context = this@CartItemActivity
    private val binding by lazy { ActivityCartItemBinding.inflate(layoutInflater) }

    private lateinit var cartItemList: ArrayList<ProductDto>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var cartItemListAdapter: CartItemListAdapter
    private lateinit var cartMap: HashMap<String, CartListDto>
    private lateinit var cartViewModel: CartViewModel

    var packageType = ""
    var offerType = ""
    var totalAmount = 0F


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
            cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
            cartItemList = ArrayList()
            cartMap = java.util.HashMap()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.btnAddCart.setOnClickListener {
                launchAddressSelectionActivity();
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchAddressSelectionActivity() {
        try {
            val intent = Intent(context, AddressSelectionActivity::class.java)
            intent.putExtra("total", totalAmount)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
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

                cartViewModel.getCartItem(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this) { jsonObject ->
                    dismissProgressBar()
                    parseCartItemsResponse(jsonObject)
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
                            val productDto = ProductDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                productDto.id = itemObject.getString("_id")
                            }

                            if (itemObject.has("productDetails") && !itemObject.isNull("productDetails")) {
                                val productDetailsObj = itemObject.getJSONObject("productDetails")

                                if (productDetailsObj.has("_id")
                                    && !productDetailsObj.isNull("_id")
                                ) {
                                    productDto.productId = productDetailsObj.getString("_id")
                                }

                                if (productDetailsObj.has("productCode") && !productDetailsObj.isNull(
                                        "productCode"
                                    )
                                ) {
                                    productDto.productCode =
                                        productDetailsObj.getString("productCode")
                                }

                                if (productDetailsObj.has("productName") && !productDetailsObj.isNull(
                                        "productName"
                                    )
                                ) {
                                    productDto.productName =
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
                                productDto.brandImage = brandImageList

                                val sliderImageList = ArrayList<String>()
                                if (productDetailsObj.has("sliderImage") && !productDetailsObj.isNull(
                                        "sliderImage"
                                    )
                                ) {
                                    val brandImageArray =
                                        productDetailsObj.getJSONArray("sliderImage")
                                    for (j in 0 until brandImageArray.length()) {
                                        sliderImageList.add(brandImageArray.getString(j))
                                    }
                                }
                                productDto.sliderImage = sliderImageList

                                if (productDetailsObj.has("brandId") && !productDetailsObj.isNull("brandId")) {
                                    productDto.brandId = productDetailsObj.getString("brandId")
                                }

                                if (productDetailsObj.has("brandName") && !productDetailsObj.isNull(
                                        "brandName"
                                    )
                                ) {
                                    productDto.brandName = productDetailsObj.getString("brandName")
                                }

                                if (productDetailsObj.has("packageType") && !productDetailsObj.isNull(
                                        "packageType"
                                    )
                                ) {
                                    productDto.packageType =
                                        productDetailsObj.getString("packageType")
                                    packageType = productDetailsObj.getString("packageType")
                                }

                                if (productDetailsObj.has("offerType")
                                    && !productDetailsObj.isNull("offerType")
                                ) {
                                    productDto.offerType = productDetailsObj.getString("offerType")
                                    offerType = productDetailsObj.getString("offerType")
                                }

                                if (productDetailsObj.has("description")
                                    && !productDetailsObj.isNull("description")
                                ) {
                                    productDto.description =
                                        productDetailsObj.getString("description")
                                }

                                if (productDetailsObj.has("createdAtTZ")
                                    && !productDetailsObj.isNull("createdAtTZ")
                                ) {
                                    productDto.createdAtTZ =
                                        productDetailsObj.getString("createdAtTZ")
                                }

                                val priceDetailsArray = ArrayList<ProductPriceDto>()
                                if (productDetailsObj.has("priceDetails")
                                    && !productDetailsObj.isNull("priceDetails")
                                ) {
                                    val priceArray = productDetailsObj.getJSONArray("priceDetails")
                                    val sortedPriceArray = sortJsonArray(priceArray)
                                    for (k in 0 until sortedPriceArray.length()) {
                                        val priceObject = sortedPriceArray.getJSONObject(k)
                                        val normalOfferPriceDto = ProductPriceDto()

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
                                productDto.priceDetails = priceDetailsArray
                            }

                            val cartList = ArrayList<CartListDto>()
                            if (itemObject.has("cart") && !itemObject.isNull("cart")) {
                                val cartArray = itemObject.getJSONArray("cart")
                                for (k in 0 until cartArray.length()) {
                                    val cartObject = cartArray.getJSONObject(k)
                                    val cartListDto = CartListDto()

                                    cartListDto.packageType = packageType

                                    cartListDto.offerType = offerType

                                    if (cartObject.has("_id") && !cartObject.isNull("_id")) {
                                        cartListDto.cartItemId = cartObject.getString("_id")
                                    }

                                    if (cartObject.has("availability") && !cartObject.isNull("availability")) {
                                        cartListDto.availability =
                                            cartObject.getInt("availability")
                                    }

                                    if (cartObject.has("measureType") && !cartObject.isNull("measureType")) {
                                        cartListDto.measureType =
                                            cartObject.getString("measureType")
                                    }

                                    if (cartObject.has("unit") && !cartObject.isNull("unit")) {
                                        cartListDto.unit = cartObject.getInt("unit")
                                    }

                                    if (cartObject.has("normalPrice") && !cartObject.isNull("normalPrice")) {
                                        cartListDto.normalPrice =
                                            cartObject.getInt("normalPrice")
                                    }

                                    if (cartObject.has("attilPrice") && !cartObject.isNull("attilPrice")) {
                                        cartListDto.attilPrice = cartObject.getInt("attilPrice")
                                    }

                                    if (cartObject.has("createdAtTZ") && !cartObject.isNull("createdAtTZ")) {
                                        cartListDto.updatedAt =
                                            CommonClass.getDateTimeFromUtc(
                                                cartObject.getString("createdAtTZ"),
                                                AppConstant.appDateFormat,
                                                AppConstant.appDateTimeFormat_TimeZone
                                            )
                                    }

                                    if (cartObject.has("updatedAtTZ") && !cartObject.isNull("updatedAtTZ")) {
                                        cartListDto.updatedAt =
                                            CommonClass.getDateTimeFromUtc(
                                                cartObject.getString("updatedAtTZ"),
                                                AppConstant.appDateFormat,
                                                AppConstant.appDateTimeFormat_TimeZone
                                            )
                                    }

                                    if (cartObject.has("selectedMin") && !cartObject.isNull("selectedMin")) {
                                        val minUnitObject = cartObject.getJSONObject("selectedMin")

                                        if (minUnitObject.has("measureType")) {
                                            cartListDto.cartMinUnitMeasureType =
                                                minUnitObject.getString("measureType")
                                        }

                                        if (minUnitObject.has("unit") && !minUnitObject.isNull("unit")) {
                                            cartListDto.cartSelectedMinUnit =
                                                minUnitObject.getInt("unit")
                                        }
                                    }

                                    if (cartObject.has("selectedMax") && !cartObject.isNull("selectedMax")) {

                                        val maxUnitObject = cartObject.getJSONObject("selectedMax")

                                        if (maxUnitObject.has("measureType")) {
                                            cartListDto.cartMaxUnitMeasureType =
                                                maxUnitObject.getString("measureType")
                                        }
                                        if (maxUnitObject.has("unit") && !maxUnitObject.isNull("unit")) {
                                            cartListDto.cartSelectedMaxUnit =
                                                maxUnitObject.getInt("unit")
                                        }
                                    }

                                    if (cartObject.has("minUnit") && !cartObject.isNull("minUnit")) {
                                        val minUnitObject = cartObject.getJSONObject("minUnit")

                                        if (minUnitObject.has("measureType")) {
                                            cartListDto.minUnitMeasureType =
                                                minUnitObject.getString("measureType")
                                        }

                                        if (minUnitObject.has("unit") && !minUnitObject.isNull("unit")) {
                                            cartListDto.minUnit =
                                                minUnitObject.getInt("unit")
                                        }
                                    }

                                    if (cartObject.has("maxUnit") && !cartObject.isNull("maxUnit")) {

                                        val maxUnitObject = cartObject.getJSONObject("maxUnit")

                                        if (maxUnitObject.has("measureType")) {
                                            cartListDto.maxUnitMeasureType =
                                                maxUnitObject.getString("measureType")
                                        }
                                        if (maxUnitObject.has("unit") && !maxUnitObject.isNull("unit")) {
                                            cartListDto.maxUnit =
                                                maxUnitObject.getInt("unit")
                                        }
                                    }

                                    if (cartObject.has("selectedItemCount") && !cartObject.isNull("selectedItemCount")) {
                                        cartListDto.cartSelectedItemCount =
                                            cartObject.getInt("selectedItemCount")
                                    }

                                    if (cartObject.has("offerDetails") && !cartObject.isNull("offerDetails")) {
                                        val offerDetailsObject =
                                            cartObject.getJSONObject("offerDetails")
                                        if (offerDetailsObject.has("measureType")) {
                                            cartListDto.offerMeasureType =
                                                offerDetailsObject.getString("measureType")
                                        }

                                        if (offerDetailsObject.has("unit") && !offerDetailsObject.isNull(
                                                "unit"
                                            )
                                        ) {
                                            cartListDto.offerUnit =
                                                offerDetailsObject.getInt("unit")
                                        }

                                        if (offerDetailsObject.has("offerPercentage")
                                            && !offerDetailsObject.isNull(
                                                "offerPercentage"
                                            )
                                        ) {
                                            cartListDto.offerPercentage =
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
                                                cartListDto.bogeProductName =
                                                    extraProdObj.getString("productName")
                                            }
                                            if (extraProdObj.has("brandImage")
                                                && !extraProdObj.isNull("brandImage")
                                            ) {
                                                val extraBrandImg =
                                                    extraProdObj.getJSONArray("brandImage")
                                                cartListDto.bogeProductImg =
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
                                                    cartListDto.bogeUnit =
                                                        extraPriceObj.getInt("unit")
                                                }
                                                if (extraPriceObj.has("measureType") && !extraPriceObj.isNull(
                                                        "measureType"
                                                    )
                                                ) {
                                                    cartListDto.bogeMeasureType =
                                                        extraPriceObj.getString("measureType")
                                                }
                                            }
                                        }
                                    }

                                    cartList.add(cartListDto)
                                }
                            }
                            productDto.cartList = cartList

                            cartItemList.add(productDto)
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

    private fun sortJsonArray(array: JSONArray): JSONArray {
        val jsons: MutableList<JSONObject?> = ArrayList()
        for (i in 0 until array.length()) {
            try {
                jsons.add(array.getJSONObject(i))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        jsons.sortWith(Comparator { lhs, rhs ->
            var lid = 0
            var rid = 0
            try {
                lid = lhs!!.getInt("unit")
                rid = rhs!!.getInt("unit")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            // Here you could parse string id to integer and then compare.
            lid.compareTo(rid)
        })
        return JSONArray(jsons)
    }

    private fun populateCartItemList() {
        try {
            initToolBar(cartItemList.size)
            if (!cartItemList.isNullOrEmpty()) {
                binding.lvProducts.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                binding.coordinatorLayout.visibility = View.VISIBLE
                linearLayoutManager = LinearLayoutManager(context)
                binding.lvProducts.layoutManager = linearLayoutManager
                cartItemListAdapter =
                    CartItemListAdapter(context, cartItemList, this@CartItemActivity)
                binding.lvProducts.adapter = cartItemListAdapter
            } else {
                binding.lvProducts.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
                binding.coordinatorLayout.visibility = View.GONE
            }
            calculateTotal(cartItemList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateTotal(cartItemList: java.util.ArrayList<ProductDto>) {
        try {
            for (k in 0 until cartItemList.size) {
                val cartItemDto = cartItemList[k]
                val cartDetailsList = cartItemDto.cartList
                for (i in 0 until cartDetailsList!!.size) {
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

    override fun selectItem(productDto: ProductDto) {
        try {
            launchItemDetailsActivity(productDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cartItemList.clear()
                getCartItems()
            }
        }

    private fun launchItemDetailsActivity(productDto: ProductDto) {
        try {
            val intent = Intent(context, ItemDetailsActivity::class.java)
            intent.putExtra("itemDto", productDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            resultLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun handleItem(
        cartListDto: CartListDto,
        isAdd: Boolean,
        itemGroupPosition: Int,
        position: Int
    ) {
        try {
            if (isAdd) {
                //Add Item From cart
                //Log.e("TAG", "Add Item")
                addItem(cartListDto, itemGroupPosition, position)
            } else {
                //Remove Item From cart
                //Log.e("TAG", "Remove Item")
                removeItem(cartListDto, itemGroupPosition, position)
                addItem(cartListDto, itemGroupPosition, position)
            }
            cartItemListAdapter.notifyItemChanged(itemGroupPosition)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun remove(position: Int) {
        try {
            val cartItemDto = cartItemList[position]
            removeCartItem(cartItemDto.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun removeCartItem(itemGroupPosition: Int, cartListDto: CartListDto) {
        try {
            cartItemList[itemGroupPosition].cartList!!.remove(cartListDto)
            calculateTotal(cartItemList)
            updateCart(cartItemList[itemGroupPosition], true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addItem(
        cartListDto: CartListDto,
        itemGroupPosition: Int,
        position: Int
    ) {
        try {
            cartItemList[itemGroupPosition].cartList!![position] = cartListDto
            calculateTotal(cartItemList)
            updateCart(cartItemList[itemGroupPosition], false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeItem(
        cartListDto: CartListDto,
        itemGroupPosition: Int,
        position: Int
    ) {
        try {
            cartItemList[itemGroupPosition].cartList!![position] = cartListDto
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

                cartViewModel.removeCartItem(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this) { jsonObject ->
                    dismissProgressBar()
                    parseRemoveCartItemResponse(jsonObject)
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

    private fun parseRemoveCartItemResponse(obj: JSONObject) {
        try {
            //Log.e("removeCartItem", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    cartItemList.clear()
                    getCartItems()
                }
            } else {
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateCart(cartItemDto: ProductDto, isRemove: Boolean) {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty(
                    "userId",
                    preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                )
                requestObject.addProperty("cartId", cartItemDto.id)
                val cartDetailsArray = JsonArray()
                val cartList = cartItemDto.cartList
                for (j in 0 until cartList!!.size) {
                    val cartDetailsDto = cartList[j]

                    val cartDetailsObject = JsonObject()
                    if (cartDetailsDto.packageType.equals("packed", true)) {
                        cartDetailsObject.addProperty(
                            "selectedItemCount",
                            cartDetailsDto.cartSelectedItemCount
                        )
                    } else if (cartItemDto.packageType.equals("loose", true)) {
                        val quantity =
                            cartDetailsDto.cartSelectedMinUnit + (cartDetailsDto.cartSelectedMaxUnit * 1000)
                        val qty = quantity.toFloat().div(1000)
                        val qtyStr = "%.3f".format(qty)

                        val minUnit = qtyStr.split(".")[1]
                        val maxUnit = qtyStr.split(".")[0]

                        val selectedMin = JsonObject()
                        selectedMin.addProperty(
                            "measureType",
                            cartDetailsDto.minUnitMeasureType
                        )
                        selectedMin.addProperty("unit", minUnit)
                        cartDetailsObject.add("selectedMin", selectedMin)

                        val selectedMax = JsonObject()
                        selectedMax.addProperty(
                            "measureType",
                            cartDetailsDto.maxUnitMeasureType
                        )
                        selectedMax.addProperty("unit", maxUnit)
                        cartDetailsObject.add("selectedMax", selectedMax)
                    }

                    val priceDetailsDto = cartItemDto.priceDetails
                    for (k in 0 until priceDetailsDto.size) {
                        cartDetailsObject.addProperty("priceId", priceDetailsDto[k].id)
                    }
                    cartDetailsArray.add(cartDetailsObject)
                }
                requestObject.add("cartDetails", cartDetailsArray)
                requestObject.addProperty("productId", cartItemDto.productId)
                initUpdateCartService(requestObject, isRemove)
                //Log.e("Request Object", requestObject.toString())
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

    private fun initUpdateCartService(requestObject: JsonObject, isRemove: Boolean) {
        try {
            showProgressBar()
            val itemDetailsViewModel = ItemDetailsViewModel()
            itemDetailsViewModel.addToCart(
                requestObject,
                preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)
            ).observe(this, { jsonObject ->
                //Log.e("Response", jsonObject.toString())
                if (jsonObject != null) {
                    dismissProgressBar()
                    parseUpdateCartResponse(jsonObject, isRemove)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseUpdateCartResponse(obj: JSONObject, isRemove: Boolean) {
        try {
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (isRemove) {
                        cartItemList.clear()
                        getCartItems()
                    }
                }
            } else {
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}