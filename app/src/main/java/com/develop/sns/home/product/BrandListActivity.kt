package com.develop.sns.home.product

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityBrandListBinding
import com.develop.sns.home.details.ItemDetailsActivity
import com.develop.sns.home.dto.NormalOfferDto
import com.develop.sns.home.dto.NormalOfferPriceDto
import com.develop.sns.home.offers.adapter.NormalOffersListAdapter
import com.develop.sns.home.offers.listener.NormalOfferListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.gson.JsonObject
import com.talentmicro.icanrefer.dto.CategoryProductDto
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class BrandListActivity : SubModuleActivity(), NormalOfferListener {

    private val context: Context = this@BrandListActivity
    private val binding by lazy { ActivityBrandListBinding.inflate(layoutInflater) }

    private lateinit var normalOfferList: ArrayList<NormalOfferDto>
    private lateinit var categoryProductDto: CategoryProductDto

    val time = 4000
    var packageType = ""
    var offerType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialiseProgressBar(binding.lnProgressbar)
        initToolBar()
        initClassReference()
        getIntentValue()
        handleUiElement()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title = resources.getString(R.string.brands)
            setSupportActionBar(binding.lnToolbar.toolbar)
            assert(supportActionBar != null)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            binding.lnToolbar.toolbar.navigationIcon = ContextCompat.getDrawable(
                context,
                R.drawable.ic_action_back
            )
            binding.lnToolbar.toolbar.layoutDirection = View.LAYOUT_DIRECTION_LTR
            binding.lnToolbar.toolbar.setNavigationOnClickListener { onBackPressed() }
        } catch (bug: Exception) {
            bug.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            language = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_LANGUAGE)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            categoryProductDto =
                intent.getSerializableExtra("categoryMainDto") as CategoryProductDto
            getNormalOffers(categoryProductDto)
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

    private fun getNormalOffers(categoryProductDto: CategoryProductDto) {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty("skip", 0)
                requestObject.addProperty("search", "")
                requestObject.addProperty("commonProductId", categoryProductDto.commonProductId)
                requestObject.addProperty("productName", categoryProductDto.productName)
                val productsViewModel = ProductsViewModel()
                productsViewModel.getProductBrands(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(this, { jsonObject ->
                    dismissProgressBar()
                    parseNormalOffersResponse(jsonObject)
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

    private fun parseNormalOffersResponse(obj: JSONObject) {
        try {
            Log.e("Brands", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    normalOfferList = ArrayList<NormalOfferDto>();
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val itemObject = dataArray.getJSONObject(i)
                            val normalOfferDto = NormalOfferDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                normalOfferDto.id = itemObject.getString("_id")
                            }

                            if (itemObject.has("productCode") && !itemObject.isNull("productCode")) {
                                normalOfferDto.productCode = itemObject.getString("productCode")
                            }

                            if (itemObject.has("productName") && !itemObject.isNull("productName")) {
                                normalOfferDto.productName = itemObject.getString("productName")
                            }

                            val brandImageList = ArrayList<String>()
                            if (itemObject.has("brandImage") && !itemObject.isNull("brandImage")) {
                                val brandImageArray = itemObject.getJSONArray("brandImage")
                                for (j in 0 until brandImageArray.length()) {
                                    brandImageList.add(brandImageArray.getString(j))
                                }
                            }
                            normalOfferDto.brandImage = brandImageList

                            if (itemObject.has("brandId") && !itemObject.isNull("brandId")) {
                                normalOfferDto.brandId = itemObject.getString("brandId")
                            }

                            if (itemObject.has("brandName") && !itemObject.isNull("brandName")) {
                                normalOfferDto.brandName = itemObject.getString("brandName")
                            }

                            if (itemObject.has("packageType") && !itemObject.isNull("packageType")) {
                                normalOfferDto.packageType = itemObject.getString("packageType")
                                packageType = itemObject.getString("packageType")
                            }

                            if (itemObject.has("offerType") && !itemObject.isNull("offerType")) {
                                normalOfferDto.offerType = itemObject.getString("offerType")
                                offerType = itemObject.getString("offerType")
                            }

                            if (itemObject.has("description") && !itemObject.isNull("description")) {
                                normalOfferDto.description = itemObject.getString("description")
                            }

                            if (itemObject.has("createdAtTZ") && !itemObject.isNull("createdAtTZ")) {
                                normalOfferDto.createdAtTZ = itemObject.getString("createdAtTZ")
                            }

                            val priceDetailsArray = ArrayList<NormalOfferPriceDto>();
                            if (itemObject.has("priceDetails") && !itemObject.isNull("priceDetails")) {
                                val priceArray = itemObject.getJSONArray("priceDetails")
                                val sortedPriceArray = sortJsonArray(priceArray);
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
                            normalOfferDto.priceDetails = priceDetailsArray

                            normalOfferList!!.add(normalOfferDto)
                        }
                    }
                    populateNormalOfferList()
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    );
                }
            } else {
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateNormalOfferList() {
        try {
            if (normalOfferList != null && !normalOfferList.isNullOrEmpty()) {
                binding.lvProductVaritiesList.visibility = View.VISIBLE
                binding.tvProductNoData.visibility = View.GONE
                binding.lvProductVaritiesList.layoutManager = LinearLayoutManager(context)
                binding.lvProductVaritiesList.adapter =
                    NormalOffersListAdapter(context, normalOfferList, this@BrandListActivity)
            } else {
                binding.lvProductVaritiesList.visibility = View.GONE
                binding.tvProductNoData.visibility = View.VISIBLE
            }
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

    override fun selectNormalOfferItem(itemDto: NormalOfferDto) {
        try {
            launchItemDetailsActivity(itemDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun launchItemDetailsActivity(itemDto: NormalOfferDto) {
        try {
            val intent = Intent(context, ItemDetailsActivity::class.java)
            intent.putExtra("itemDto", itemDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}