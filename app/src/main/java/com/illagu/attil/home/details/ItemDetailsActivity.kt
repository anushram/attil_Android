package com.illagu.attil.home.details

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.illagu.attil.R
import com.illagu.attil.SubModuleActivity
import com.illagu.attil.databinding.ActivityItemDetailsBinding
import com.illagu.attil.home.details.adapter.ItemDetailsListAdapter
import com.illagu.attil.home.offers.adapter.SliderAdapter
import com.illagu.attil.home.offers.dto.ProductDto
import com.illagu.attil.home.offers.dto.ProductPriceDto
import com.illagu.attil.home.offers.listener.ItemListener
import com.illagu.attil.home.product.BrandListActivity
import com.illagu.attil.home.product.VarietyListActivity
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.AppUtils
import com.illagu.attil.utils.CommonClass
import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import org.json.JSONObject


class ItemDetailsActivity : SubModuleActivity(), ItemListener {

    private val context: Context = this@ItemDetailsActivity
    private val binding by lazy { ActivityItemDetailsBinding.inflate(layoutInflater) }

    var itemMainDto: ProductDto? = null
    private var itemDetailsListAdapter: ItemDetailsListAdapter? = null
    private lateinit var cartMap: HashMap<String, ProductPriceDto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        initClassReference()
        getIntentValue()
        handleUiElement()
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            itemMainDto = intent.getSerializableExtra("itemDto") as ProductDto?

            val cartList = itemMainDto!!.cartList
            if (cartList != null && cartList.isNotEmpty()) {
                val productPriceList = itemMainDto!!.priceDetails
                for (j in 0 until productPriceList.size) {
                    val productPriceDto = productPriceList[j]
                    for (i in 0 until cartList.size) {
                        val cartListDto = cartList[i]
                        if (productPriceDto.unit == cartListDto.unit) {
                            if (cartListDto.packageType == "loose" && cartListDto.offerType == "normal") {
                                val qty =
                                    cartListDto.cartSelectedMinUnit + (cartListDto.cartSelectedMaxUnit * 1000)
                                productPriceDto.quantity = qty
                            } else {
                                productPriceDto.quantity = cartListDto.cartSelectedItemCount
                            }
                            itemMainDto!!.priceDetails[j] = productPriceDto
                            addItem(productPriceDto)
                        }
                    }
                }
            }
            populateUiElement()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            cartMap = HashMap()
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
                if (!cartMap.isEmpty()) {
                    addToCart()
                } else {
                    showErrorAlert()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showErrorAlert() {
        try {
            var announcementDialog: AlertDialog? = null
            val li = LayoutInflater.from(this@ItemDetailsActivity)
            val promptsView: View = li.inflate(R.layout.info_dialog_layout, null)
            val builder = AlertDialog.Builder(this@ItemDetailsActivity)
            builder.setView(promptsView)

            val btnOk = promptsView.findViewById<Button>(R.id.btn_got_it)
            btnOk.setOnClickListener {
                announcementDialog!!.dismiss()
            }
            announcementDialog = builder.create()
            announcementDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            announcementDialog.setCanceledOnTouchOutside(false)
            announcementDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            announcementDialog.setCancelable(false)
            announcementDialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun addToCart() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                if (itemMainDto!!.packageType.equals("packed", true)) {
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "userId",
                        preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                    )
                    requestObject.add("cartId", JsonNull.INSTANCE)

                    val cartDetailsArray = JsonArray()
                    if (cartMap.isNotEmpty()) {
                        for ((_, value) in cartMap) {
                            val cartDetailsObject = JsonObject()
                            cartDetailsObject.addProperty("selectedItemCount", value.quantity)
                            cartDetailsObject.addProperty("priceId", value.id)
                            cartDetailsArray.add(cartDetailsObject)
                        }
                    }
                    requestObject.add("cartDetails", cartDetailsArray)

                    requestObject.addProperty("productId", itemMainDto!!.id)

                    //Log.e("Packed Object", requestObject.toString())
                    initService(requestObject)

                } else if (itemMainDto!!.packageType.equals("loose", true)) {
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "userId",
                        preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                    )
                    requestObject.add("cartId", JsonNull.INSTANCE)
                    requestObject.addProperty("productId", itemMainDto!!.id)

                    val cartDetailsArray = JsonArray()
                    if (cartMap.isNotEmpty()) {
                        for ((_, value) in cartMap) {
                            val cartDetailsObject = JsonObject()

                            val qty = value.quantity.toFloat().div(1000)
                            val qtyStr = "%.3f".format(qty)

                            val minUnit = qtyStr.split(".")[1]
                            val maxUnit = qtyStr.split(".")[0]

                            val selectedMin = JsonObject()
                            selectedMin.addProperty("measureType", value.minUnitMeasureType)
                            selectedMin.addProperty("unit", minUnit)
                            cartDetailsObject.add("selectedMin", selectedMin)

                            val selectedMax = JsonObject()
                            selectedMax.addProperty("measureType", value.maxUnitMeasureType)
                            selectedMax.addProperty("unit", maxUnit)
                            cartDetailsObject.add("selectedMax", selectedMax)

                            cartDetailsObject.addProperty("priceId", value.id)
                            cartDetailsArray.add(cartDetailsObject)
                        }
                    }
                    requestObject.add("cartDetails", cartDetailsArray)

                    //Log.e("Loose Object", requestObject.toString())
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

    private fun initService(requestObject: JsonObject) {
        try {
            showProgressBar()
            val itemDetailsViewModel = ItemDetailsViewModel(application)
            itemDetailsViewModel.addToCart(
                requestObject,
                preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)
            ).observe(this) { jsonObject ->
                //Log.e("Response", jsonObject.toString())
                if (jsonObject != null) {
                    dismissProgressBar()
                    parseAddCartResponse(jsonObject)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseAddCartResponse(jsonObject: JSONObject) {
        try {
            if (jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                cartMap.clear()
                cartMap = HashMap()
                calculateTotal(cartMap)
                handleResponse()
            } else {
                CommonClass.handleErrorResponse(context, jsonObject, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateUiElement() {
        try {
            if (itemMainDto != null) {

                binding.tvProductName.text = itemMainDto!!.productName
                if (itemMainDto!!.brandName.trim().isNotEmpty()) {
                    binding.tvBrandName.visibility = View.VISIBLE
                    binding.tvBrandName.text = itemMainDto!!.brandName
                } else {
                    binding.tvBrandName.visibility = View.GONE
                }

                val imageHeight = CommonClass.getScreenWidth(this@ItemDetailsActivity) / 1.78
                //Log.e("imageHeight", imageHeight.toString())
                binding.rlView.requestLayout()
                binding.rlView.layoutParams.height = imageHeight.toInt()

                val imageList = itemMainDto?.sliderImage
                populateImageList(imageList)

                populateItemList(itemMainDto!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateItemList(itemDto: ProductDto) {
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
                binding.imageSlider.setSliderAdapter(adapter, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectItem(
        position: Int,
        productPriceDto: ProductPriceDto,
        isSelect: Boolean
    ) {
        try {
            val quantity: Int = productPriceDto.quantity
            //Log.e("selectItem", quantity.toString())
            if (itemDetailsListAdapter != null) {
                productPriceDto.selectedFlag = isSelect

                if (itemMainDto!!.packageType.equals("loose", true)
                    && itemMainDto!!.offerType.equals("normal", true)
                ) {
                    if (quantity.toFloat() <= productPriceDto.maxUnit * 1000.toFloat()) {
                        if (quantity.toFloat() < productPriceDto.minUnit.toFloat()) {
                            productPriceDto.quantity = productPriceDto.minUnit
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
                    if (quantity.toFloat() <= productPriceDto.availability.toFloat()) {
                        productPriceDto.quantity = quantity + 1
                    }
                }
                itemDetailsListAdapter!!.notifyItemChanged(position, productPriceDto)
                if (isSelect) {
                    addItem(productPriceDto)
                } else {
                    removeItem(productPriceDto)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun changeCount(position: Int, itemDto: ProductPriceDto, isAdd: Boolean) {
        try {
            if (itemDetailsListAdapter != null) {
                var quantity: Int = itemDto.quantity
                quantity = if (isAdd) {
                    val value: Int = quantity + 1
                    value
                } else {
                    val value: Int = quantity - 1
                    value
                }
                if (quantity.toFloat() <= itemDto.availability.toFloat()) {
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun changeCountGmOrKg(
        position: Int,
        productPriceDto: ProductPriceDto,
        isAdd: Boolean,
        isGm: Boolean,
        count: Int,
    ) {
        //Log.e("Count", count.toString())
        try {
            if (itemDetailsListAdapter != null) {
                var quantity: Int = productPriceDto.quantity

                val qty = quantity.toFloat().div(1000)
                val qtyStr = "%.3f".format(qty)

                val minUnit = qtyStr.split(".")[1]
                val maxUnit = qtyStr.split(".")[0]

                if (isGm) {
                    var minQuantity = Integer.parseInt(minUnit)
                    minQuantity = if (isAdd) {
                        if (count == 1) {
                            val value: Int = productPriceDto.minUnit
                            value
                        } else {
                            val value: Int = minQuantity + 50
                            value
                        }
                    } else {
                        val value: Int = minQuantity - 50
                        value
                    }

                    var maxQuantity = Integer.parseInt(maxUnit)
                    quantity = minQuantity + (maxQuantity * 1000)
                    if (quantity.toFloat() < productPriceDto.maxUnit * 1000.toFloat()) {
                        if (quantity.toFloat() < productPriceDto.minUnit.toFloat()) {
                            //Log.e("Less Than", "Min")
                            //Log.e("Less Than", "Comes Here")
                            productPriceDto.selectedFlag = false
                            productPriceDto.quantity = 0
                            removeItem(productPriceDto)
                            ItemDetailsListAdapter.clickGmPlusCount = 0
                        } else {
                            productPriceDto.selectedFlag = true
                            maxQuantity = Integer.parseInt(maxUnit)
                            val qty2 = minQuantity + (maxQuantity * 1000)
                            productPriceDto.quantity = qty2
                            removeItem(productPriceDto)
                            addItem(productPriceDto)
                        }
                    } else {
                        productPriceDto.selectedFlag = true
                        maxQuantity = Integer.parseInt(maxUnit)
                        val qty3 = (maxQuantity * 1000)
                        productPriceDto.quantity = qty3
                        addItem(productPriceDto)
                    }
                    itemDetailsListAdapter!!.notifyItemChanged(position, productPriceDto)
                } else {
                    var maxQuantity = Integer.parseInt(maxUnit)
                    maxQuantity = if (isAdd) {
                        val value: Int = maxQuantity + 1
                        value
                    } else {
                        val value: Int = maxQuantity - 1
                        value
                    }
                    if (maxQuantity.toFloat() < productPriceDto.maxUnit.toFloat()) {
                        productPriceDto.selectedFlag = true
                        val minQuantity = Integer.parseInt(minUnit)
                        val qty4 = minQuantity + (maxQuantity * 1000)
                        productPriceDto.quantity = qty4
                        addItem(productPriceDto)
                    } else {
                        productPriceDto.selectedFlag = false
                        productPriceDto.quantity = productPriceDto.maxUnit * 1000
                        removeItem(productPriceDto)
                        addItem(productPriceDto)
                    }
                    itemDetailsListAdapter!!.notifyItemChanged(position, productPriceDto)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun addItemFromCart(position: Int, productPriceDto: ProductPriceDto) {
        try {
            addItem(productPriceDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addItem(productPriceDto: ProductPriceDto) {
        try {
            if (cartMap.containsKey(productPriceDto.id)) {
                cartMap.remove(productPriceDto.id)
            }
            cartMap[productPriceDto.id] = productPriceDto
            calculateTotal(cartMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeItem(productPriceDto: ProductPriceDto) {
        try {
            cartMap.remove(productPriceDto.id)
            calculateTotal(cartMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateTotal(cartMap: HashMap<String, ProductPriceDto>) {
        try {
            //Log.e("Calculate", cartMap.size.toString())
            var totalPrice: Float = 0F
            if (cartMap.isNotEmpty()) {
                for ((key, value) in cartMap) {
                    println(
                        "$key = ${
                            value.packageType.plus(" ").plus(value.offerType).plus(" ")
                                .plus(value.quantity.toString()).plus(" ")
                                .plus(value.attilPrice)
                        }"
                    )
                    if (value.packageType == "loose" && value.offerType == "normal") {
                        val result = value.quantity.toFloat() / value.unit.toFloat()
                        totalPrice += result.times(value.attilPrice)
                    } else {
                        totalPrice += value.quantity * value.attilPrice
                        //Log.e("tot", totalPrice.toString())
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
            if (VarietyListActivity().fa != null) {
                VarietyListActivity().fa!!.finish()
            }
            if (BrandListActivity().fa != null) {
                BrandListActivity().fa!!.finish()
            }
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        cartMap.clear()
        cartMap = HashMap()
        calculateTotal(cartMap)
        super.onBackPressed()
    }

}