package com.develop.sns.home.details

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityItemDetailsBinding
import com.develop.sns.home.details.adapter.ItemDetailsListAdapter
import com.develop.sns.home.dto.NormalOfferDto
import com.develop.sns.home.dto.NormalOfferPriceDto
import com.develop.sns.home.offers.adapter.SliderAdapter
import com.develop.sns.home.offers.listener.ItemListener
import com.develop.sns.networkhandler.AppUrlManager
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import org.json.JSONArray
import org.json.JSONObject


class ItemDetailsActivity : SubModuleActivity(), ItemListener {

    private val context: Context = this@ItemDetailsActivity
    private val binding by lazy { ActivityItemDetailsBinding.inflate(layoutInflater) }

    override var preferenceHelper: PreferenceHelper? = null
    var itemMainDto: NormalOfferDto? = null
    private var itemDetailsListAdapter: ItemDetailsListAdapter? = null
    private lateinit var cartMap: HashMap<String, NormalOfferPriceDto>
    private var itemDetailsViewModel: ItemDetailsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setContentView(binding.root)

        initialiseProgressBar(R.id.rl_progress_main)
        initClassReference()
        getIntentValue()
        handleUiElement()
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            itemMainDto = intent.getSerializableExtra("itemDto") as NormalOfferDto?
            populateUiElement()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            itemDetailsViewModel = ItemDetailsViewModel()
            cartMap = CommonClass.getCartMap(context)
            calculateTotal(cartMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnBack.setOnClickListener {
                onBackPressed()
            }
            binding.btnAddCart.setOnClickListener {
                addToCart()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addToCart() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                if (itemMainDto!!.packageType.equals("packed", true)) {
                    val requestObject = JSONObject()
                    requestObject.put(
                        "userId",
                        preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                    )
                    requestObject.put("cartId", JSONObject.NULL)

                    val cartDetailsArray = JSONArray()
                    if (!cartMap.isEmpty()) {
                        for ((_, value) in cartMap) {
                            val cartDetailsObject = JSONObject()
                            cartDetailsObject.put("selectedItemCount", value.quantity)
                            cartDetailsObject.put("priceId", value.id)
                            cartDetailsArray.put(cartDetailsObject)
                        }
                    }
                    requestObject.put("cartDetails", cartDetailsArray)

                    requestObject.put("productId", itemMainDto!!.id)

                    Log.e("Packed Object", requestObject.toString())
                    initService(requestObject)

                } else if (itemMainDto!!.packageType.equals("loose", true)) {
                    val requestObject = JSONObject()
                    requestObject.put(
                        "userId",
                        preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                    )
                    requestObject.put("cartId", JSONObject.NULL)
                    requestObject.put("productId", itemMainDto!!.id)

                    val cartDetailsArray = JSONArray()
                    if (cartMap.isNotEmpty()) {
                        for ((_, value) in cartMap) {
                            val cartDetailsObject = JSONObject()

                            val qty = value.quantity?.toFloat()?.div(1000)
                            val qtyStr = "%.3f".format(qty)

                            val minUnit = qtyStr.split(".")[1]
                            val maxUnit = qtyStr.split(".")[0]

                            val selectedMin = JSONObject()
                            selectedMin.put("measureType", value.minUnitMeasureType)
                            selectedMin.put("unit", minUnit)
                            cartDetailsObject.put("selectedMin", selectedMin)

                            val selectedMax = JSONObject()
                            selectedMax.put("measureType", value.maxUnitMeasureType)
                            selectedMax.put("unit", maxUnit)
                            cartDetailsObject.put("selectedMax", selectedMax)

                            cartDetailsObject.put("priceId", value.id)
                            cartDetailsArray.put(cartDetailsObject)
                        }
                    }
                    requestObject.put("cartDetails", cartDetailsArray)

                    Log.e("Loose Object", requestObject.toString())
                    initService(requestObject)
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

    private fun initService(requestObject: JSONObject?) {
        try {
            Log.e("initService", "Comes")
            val url = AppUrlManager.getAPIUrl().toString() + "customer/addtoCart"
            showProgressBar()
            itemDetailsViewModel?.addToCart(
                url,
                AppConstant.REST_CALL_POST,
                requestObject,
                preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)
            )?.observe(this, { jsonObject ->
                Log.e("Response", jsonObject.toString())
                if (jsonObject != null) {
                    dismissProgressBar()
                    parseAddCartResponse(jsonObject)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseAddCartResponse(jsonObject: JSONObject) {
        try {
            CommonClass.removeCartMap(context)
            cartMap = CommonClass.getCartMap(context)
            calculateTotal(cartMap)
            handleResponse()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateUiElement() {
        try {
            if (itemMainDto != null) {

                binding.tvProductName.text = itemMainDto!!.productName
                binding.tvBrandName.text = itemMainDto!!.brandName

                var imageHeight = CommonClass.getScreenWidth(this@ItemDetailsActivity) / 1.78
                Log.e("imageHeight", imageHeight.toString())
                binding.rlView.requestLayout()
                binding.rlView.layoutParams.height = imageHeight.toInt()

                val imageList = itemMainDto?.brandImage
                populateImageList(imageList)

                populateItemList(itemMainDto!!)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateItemList(itemDto: NormalOfferDto) {
        try {
            binding.lvProducts.layoutManager = LinearLayoutManager(context)

            itemDetailsListAdapter = ItemDetailsListAdapter(
                context,
                itemDto,
                itemDto.priceDetails,
                this@ItemDetailsActivity
            )
            binding.lvProducts.adapter = itemDetailsListAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateImageList(imageList: ArrayList<String>?) {
        try {
            if (imageList != null && !imageList.isEmpty()) {
                val adapter = SliderAdapter(
                    this,
                    imageList
                )
                binding.imageSlider.setSliderAdapter(adapter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectItem(position: Int, itemDto: NormalOfferPriceDto?, isSelect: Boolean) {
        try {
            val quantity: Int = itemDto!!.quantity!!
            if (itemDetailsListAdapter != null) {
                itemDto!!.selectedFlag = isSelect

                if (itemMainDto!!.packageType.equals("loose")
                    && itemMainDto!!.offerType.equals("normal", true)
                ) {
                    if (quantity.toFloat() <= itemDto.maxUnit!! * 1000.toFloat()) {
                        if (quantity.toFloat() < itemDto.minUnit!!.toFloat()) {
                            itemDto.quantity = itemDto.minUnit
                        }
                    }
                } else if ((itemMainDto!!.packageType.equals("packed")
                            && itemMainDto!!.offerType.equals("normal", true))
                    || (itemMainDto!!.packageType.equals("packed")
                            && itemMainDto!!.offerType.equals("special", true))
                    || (itemMainDto!!.packageType.equals("packed")
                            && itemMainDto!!.offerType.equals("BOGO", true))
                    || (itemMainDto!!.packageType.equals("packed")
                            && itemMainDto!!.offerType.equals("BOGE", true))
                ) {
                    if (quantity.toFloat() <= itemDto.availability!!.toFloat()) {
                        itemDto.quantity = quantity + 1
                    }
                }
                itemDetailsListAdapter!!.notifyItemChanged(position, itemDto);
                if (isSelect) {
                    addItem(itemDto);
                } else {
                    removeItem(itemDto);
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun changeCount(position: Int, itemDto: NormalOfferPriceDto?, isAdd: Boolean) {
        try {
            if (itemDetailsListAdapter != null) {
                var quantity: Int = itemDto!!.quantity!!
                if (itemMainDto!!.packageType.equals(
                        "loose",
                        true
                    ) && itemMainDto!!.offerType.equals(
                        "normal",
                        true
                    )
                ) {
                    quantity = if (isAdd) {
                        val value: Int = quantity + 50
                        value
                    } else {
                        val value: Int = quantity - 50
                        value
                    }
                    if (quantity.toFloat() <= itemDto.maxUnit!! * 1000.toFloat()) {
                        if (quantity.toFloat() < itemDto.minUnit!!.toFloat()) {
                            itemDto!!.selectedFlag = false
                            itemDto.quantity = itemDto.minUnit
                            removeItem(itemDto)
                        } else {
                            itemDto!!.selectedFlag = true
                            itemDto.quantity = quantity
                            addItem(itemDto)
                        }
                        itemDetailsListAdapter!!.notifyItemChanged(position, itemDto)
                    }
                } else if ((itemMainDto!!.packageType.equals("packed", true)
                            && itemMainDto!!.offerType.equals("normal", true))
                    || (itemMainDto!!.packageType.equals("packed", true)
                            && itemMainDto!!.offerType.equals("special", true))
                    || (itemMainDto!!.packageType.equals("packed", true)
                            && itemMainDto!!.offerType.equals("BOGO", true))
                    || (itemMainDto!!.packageType.equals("packed", true)
                            && itemMainDto!!.offerType.equals("BOGE", true))
                ) {
                    quantity = if (isAdd) {
                        val value: Int = quantity + 1
                        value
                    } else {
                        val value: Int = quantity - 1
                        value
                    }
                    if (quantity.toFloat() <= itemDto.availability!!.toFloat()) {
                        itemDto.selectedFlag = true
                        itemDto.quantity = quantity
                        addItem(itemDto)
                    } else {
                        itemDto.selectedFlag = false
                        itemDto.quantity = itemDto.availability
                        removeItem(itemDto)
                        addItem(itemDto)
                    }
                    itemDetailsListAdapter!!.notifyItemChanged(position, itemDto)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun addItem(normalOfferPriceDto: NormalOfferPriceDto) {
        try {
            if (cartMap.containsKey(normalOfferPriceDto.id)) {
                cartMap.remove(normalOfferPriceDto.id)
            }
            cartMap.put(normalOfferPriceDto.id, normalOfferPriceDto)
            CommonClass.saveCartMap(context, cartMap)
            cartMap = CommonClass.getCartMap(context)
            calculateTotal(cartMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeItem(normalOfferPriceDto: NormalOfferPriceDto) {
        try {
            cartMap.remove(normalOfferPriceDto.id)
            CommonClass.saveCartMap(context, cartMap)
            cartMap = CommonClass.getCartMap(context)
            calculateTotal(cartMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateTotal(cartMap: HashMap<String, NormalOfferPriceDto>) {
        try {
            var totalPrice: Float = 0F
            if (cartMap != null && !cartMap.isEmpty()) {
                for ((key, value) in cartMap) {
                    println("$key = ${value.quantity.toString().plus(" ").plus(value.attilPrice)}")
                    if (value.packageType.equals("loose", true) && value.offerType.equals(
                            "normal",
                            true
                        )
                    ) {
                        val result = value.quantity!!.toFloat() / value.unit!!.toFloat()
                        totalPrice += result.times(value.attilPrice!!)
                    } else if ((value.packageType.equals("packed", true)
                                && value.offerType.equals("normal", true))
                        || (value.packageType.equals("packed", true)
                                && value.offerType.equals("special", true))
                        || (value.packageType.equals("packed", true)
                                && value.offerType.equals("BOGO", true))
                        || (value.packageType.equals("packed", true)
                                && value.offerType.equals("BOGE", true))
                    ) {
                        totalPrice += value.quantity!! * value.attilPrice!!
                    }
                    binding.tvTotalPrice.text =
                        getString(R.string.Rs).plus(" ").plus("%.2f".format(totalPrice))
                }
            } else {
                binding.tvTotalPrice.text =
                    getString(R.string.Rs).plus(" ").plus("%.2f".format(totalPrice).toString())
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

    override fun takeActions(position: Int) {
        TODO("Not yet implemented")
    }

}