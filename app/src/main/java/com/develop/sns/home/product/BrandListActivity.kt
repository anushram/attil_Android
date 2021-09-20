package com.develop.sns.home.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityBrandListBinding
import com.develop.sns.home.details.ItemDetailsActivity
import com.develop.sns.home.offers.dto.NormalOfferDto
import com.develop.sns.home.offers.dto.NormalOfferPriceDto
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

    private lateinit var searchPlate: EditText
    private lateinit var close: ImageView

    private var serviceFlag = false
    private var searchQueryFlag = false
    private var searchQuery = ""

    private val limit = 20
    private var startPage = 0

    private var scrollPosition = 0
    private var scrollSelectedPosition = 0
    private var totalCount = 0

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var normalOffersListAdapter: NormalOffersListAdapter

    var fa: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fa = this;

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
            normalOfferList = ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            categoryProductDto =
                intent.getSerializableExtra("categoryMainDto") as CategoryProductDto
            getNormalOffers()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            searchPlate = binding.svSearch.findViewById(R.id.search_src_text) as EditText
            close = binding.svSearch.findViewById(R.id.search_close_btn) as ImageView
            searchPlate.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    searchQuery = v.text.toString()
                    serviceFlag = false
                    searchQueryFlag = true
                    resetPagination()
                }
                false
            }

            binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchQuery = query
                    serviceFlag = false
                    searchQueryFlag = true
                    binding.svSearch.clearFocus()
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    resetPagination()
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isEmpty()) {
                        searchQuery = newText
                        serviceFlag = false
                        binding.svSearch.clearFocus()
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        searchQueryFlag = true
                        resetPagination()
                    }
                    return false
                }
            })

            binding.lvProductVaritiesList.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount: Int = linearLayoutManager.childCount
                    val totalItemCount: Int = linearLayoutManager.itemCount
                    val firstVisibleItemPosition: Int =
                        linearLayoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition: Int =
                        linearLayoutManager.findLastVisibleItemPosition()
                    val rvRect = Rect()
                    binding.lvProductVaritiesList.getGlobalVisibleRect(rvRect)
                    scrollPosition = firstVisibleItemPosition

                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= limit) {
                        scrollSelectedPosition = lastVisibleItemPosition
                        scrollPosition = scrollSelectedPosition
                    }
                }

                override fun onScrollStateChanged(
                    @NonNull recyclerView: RecyclerView,
                    newState: Int,
                ) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Log.d("-----", "end");
                        startPage++
                        Log.e("StartPage", startPage.toString())
                        getNormalOffers()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun resetPagination() {
        try {
            this.startPage = 0
            this.scrollSelectedPosition = 0
            this.scrollPosition = 0
            this.totalCount = 0
            if (!searchQueryFlag) {
                searchQuery = ""
                searchPlate.setText("")
                searchPlate.clearFocus()
            }
            normalOfferList = ArrayList()
            this.normalOfferList.clear()
            Log.e("ResetSize", normalOfferList.size.toString())
            normalOffersListAdapter.notifyDataSetChanged()
            getNormalOffers()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getNormalOffers() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty("skip", startPage)
                requestObject.addProperty("search", searchQuery)
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
                    if (obj.has("data") && !obj.isNull("data")) {
                        serviceFlag = false
                        searchQueryFlag = false
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
                }
            }
            populateNormalOfferList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateNormalOfferList() {
        try {
            if (!normalOfferList.isNullOrEmpty()) {
                binding.lvProductVaritiesList.visibility = View.VISIBLE
                binding.tvProductNoData.visibility = View.GONE
                linearLayoutManager = LinearLayoutManager(context)
                binding.lvProductVaritiesList.layoutManager = linearLayoutManager
                normalOffersListAdapter =
                    NormalOffersListAdapter(context, normalOfferList, this@BrandListActivity)
                binding.lvProductVaritiesList.adapter = normalOffersListAdapter
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