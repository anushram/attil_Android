package com.develop.sns.home.offers

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.customviews.GravitySnapHelper
import com.develop.sns.databinding.FragmentOffersBinding
import com.develop.sns.home.details.ItemDetailsActivity
import com.develop.sns.home.offers.adapter.NormalOffersListAdapter
import com.develop.sns.home.offers.adapter.TopOffersListAdapter
import com.develop.sns.home.offers.dto.NormalOfferDto
import com.develop.sns.home.offers.dto.NormalOfferPriceDto
import com.develop.sns.home.offers.listener.NormalOfferListener
import com.develop.sns.home.offers.listener.TopOfferListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class OffersFragment : Fragment(), TopOfferListener, NormalOfferListener {

    private val binding by lazy { FragmentOffersBinding.inflate(layoutInflater) }
    private lateinit var preferenceHelper: PreferenceHelper

    private lateinit var topOfferList: ArrayList<NormalOfferDto>
    private lateinit var normalOfferList: ArrayList<NormalOfferDto>

    private lateinit var normalOffersListAdapter: NormalOffersListAdapter

    val time = 4000
    var packageType = ""
    var offerType = ""
    var language = ""

    private var serviceFlag = false
    private var searchQueryFlag = false
    private var isClose = false
    private var searchQuery = ""

    private val limit = 20
    private var startPage = 0

    private var scrollPosition = 0
    private var scrollSelectedPosition = 0
    private var totalCount = 0

    private lateinit var searchPlate: EditText
    private lateinit var close: ImageView

    private lateinit var linearLayoutManager: LinearLayoutManager

    var filterType = 0
    var filterPrice = 0
    var filterView = 0

    private var isOnActivityRes = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClassReference()
        handleUiElement()
    }

    override fun onResume() {
        Log.e("onResume", "Comes")
        super.onResume()
        binding.svSearch.clearFocus()
        hideKeyboard()
        if (!isOnActivityRes) {
            callApi()
        }
    }

    private fun callApi() {
        try {
            getTopOffers()
            getNormalOffers()
            getCartCount()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            binding.srlList.isRefreshing = false
            binding.srlList.isEnabled = false
            language =
                preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_LANGUAGE).toString()
            normalOfferList = ArrayList();
            topOfferList = ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideKeyboard() {
        try {
            val view = requireActivity().currentFocus
            if (view != null) {
                (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnFilter.setOnClickListener {
                launchFilterActivity()
            }

            binding.svSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    // searchView expanded
                    binding.lnTopOffers.visibility = View.GONE
                } else {
                    // searchView not expanded
                    binding.lnTopOffers.visibility = View.VISIBLE
                }
            }

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
                    Log.e("OnQuery", "Submit")
                    searchQuery = query
                    serviceFlag = false
                    searchQueryFlag = true
                    binding.svSearch.clearFocus()
                    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    resetPagination()
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    Log.e("OnQuery", "Change")
                    if (newText.isEmpty()) {
                        searchQuery = newText
                        serviceFlag = false
                        binding.svSearch.clearFocus()
                        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        searchQueryFlag = true
                        resetPagination()
                    }
                    return false
                }
            })

            binding.lvNormalOffers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount: Int = linearLayoutManager.childCount
                    val totalItemCount: Int = linearLayoutManager.itemCount
                    val firstVisibleItemPosition: Int =
                        linearLayoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition: Int =
                        linearLayoutManager.findLastVisibleItemPosition()
                    val rvRect = Rect()
                    binding.lvNormalOffers.getGlobalVisibleRect(rvRect)
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

    var filterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.e("onACR", "Comes")
            isOnActivityRes = true
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                filterType = data!!.getIntExtra("filterType", 0)
                filterPrice = data.getIntExtra("filterPrice", 0)
                filterView = data.getIntExtra("filterView", 0)
                resetPagination()
            }
        }

    private fun launchFilterActivity() {
        try {
            val intent = Intent(context, FilterActivity::class.java)
            intent.putExtra("filterType", filterType)
            intent.putExtra("filterPrice", filterPrice)
            intent.putExtra("filterView", filterView)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            filterLauncher.launch(intent)
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
            getNormalOffers()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getTopOffers() {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("skip", 0)
                Log.e("requestObj", requestObject.toString())
                showProgressBar()
                val offersViewModel = OffersViewModel()
                offersViewModel.getTopOffers(
                    requestObject,
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!)
                    .observe(viewLifecycleOwner, { jsonObject ->
                        parseTopOffersResponse(jsonObject)

                    })
            } else {
                CommonClass.showToastMessage(
                    requireActivity(),
                    binding.rootView,
                    resources.getString(R.string.no_internet),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseTopOffersResponse(obj: JSONObject) {
        try {
            Log.e("TopOffers", obj.toString())
            var packageType = ""
            var offerType = ""
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val itemObject = dataArray.getJSONObject(i)
                            val topOfferDto = NormalOfferDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                topOfferDto.id = itemObject.getString("_id")
                            }

                            if (itemObject.has("productCode") && !itemObject.isNull("productCode")) {
                                topOfferDto.productCode = itemObject.getString("productCode")
                            }

                            if (itemObject.has("productName") && !itemObject.isNull("productName")) {
                                topOfferDto.productName = itemObject.getString("productName")
                            }

                            val brandImageList = ArrayList<String>()
                            if (itemObject.has("brandImage") && !itemObject.isNull("brandImage")) {
                                val brandImageArray = itemObject.getJSONArray("brandImage")
                                for (j in 0 until brandImageArray.length()) {
                                    brandImageList.add(brandImageArray.getString(j))
                                }
                            }
                            topOfferDto.brandImage = brandImageList

                            if (itemObject.has("brandId") && !itemObject.isNull("brandId")) {
                                topOfferDto.brandId = itemObject.getString("brandId")
                            }

                            if (itemObject.has("brandName") && !itemObject.isNull("brandName")) {
                                topOfferDto.brandName = itemObject.getString("brandName")
                            }

                            if (itemObject.has("packageType") && !itemObject.isNull("packageType")) {
                                topOfferDto.packageType = itemObject.getString("packageType")
                                packageType = itemObject.getString("packageType")
                            }

                            if (itemObject.has("offerType") && !itemObject.isNull("offerType")) {
                                topOfferDto.offerType = itemObject.getString("offerType")
                                offerType = itemObject.getString("offerType")
                            }

                            if (itemObject.has("description") && !itemObject.isNull("description")) {
                                topOfferDto.description = itemObject.getString("description")
                            }

                            if (itemObject.has("createdAtTZ") && !itemObject.isNull("createdAtTZ")) {
                                topOfferDto.createdAtTZ = itemObject.getString("createdAtTZ")
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

                                    if (priceObject.has("unit") && !priceObject.isNull("unit")) {
                                        normalOfferPriceDto.unit =
                                            priceObject.getInt("unit")
                                    }

                                    if (priceObject.has("availability") && !priceObject.isNull("availability")) {
                                        normalOfferPriceDto.availability =
                                            priceObject.getInt("availability")
                                    }

                                    if (priceObject.has("offerDetails") && !priceObject.isNull("offerDetails")) {
                                        val offerDetailsObject =
                                            priceObject.getJSONObject("offerDetails")

                                        if (offerDetailsObject.has("measureType")) {
                                            normalOfferPriceDto.offerMeasureType =
                                                offerDetailsObject.getString("measureType")
                                        }

                                        if (offerDetailsObject.has("unit")
                                            && !offerDetailsObject.isNull("unit")
                                        ) {
                                            normalOfferPriceDto.offerUnit =
                                                offerDetailsObject.getInt("unit")
                                        }

                                        if (offerDetailsObject.has("offerPercentage")
                                            && !offerDetailsObject.isNull("offerPercentage")
                                        ) {
                                            normalOfferPriceDto.offerPercentage =
                                                offerDetailsObject.getInt("offerPercentage")
                                        }
                                    }

                                    priceDetailsArray.add(normalOfferPriceDto)
                                }
                            }
                            topOfferDto.priceDetails = priceDetailsArray

                            topOfferList.add(topOfferDto)
                        }
                    }
                    populateTopOfferList()
                } else {
                    CommonClass.showToastMessage(
                        requireActivity(),
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    );
                }
            } else {
                CommonClass.handleErrorResponse(requireActivity(), obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateTopOfferList() {
        try {
            binding.lnTopOffers.visibility = View.VISIBLE
            if (topOfferList != null && !topOfferList.isNullOrEmpty()) {
                binding.lvTopOffers.visibility = View.VISIBLE
                binding.tvTopOfferNoData.visibility = View.GONE
                val gridLayoutManager =
                    LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
                binding.lvTopOffers.layoutManager = gridLayoutManager

                val screenWidth = CommonClass.getScreenWidth(requireActivity())
                val topOffersListAdapter = TopOffersListAdapter(
                    requireActivity(),
                    topOfferList,
                    this@OffersFragment,
                    screenWidth
                )
                binding.lvTopOffers.adapter = topOffersListAdapter
                val snapHelper = GravitySnapHelper(Gravity.START)
                snapHelper.attachToRecyclerView(binding.lvTopOffers)
                binding.lvTopOffers.autoScroll(topOfferList!!.size, 1000)
                binding.lvTopOffers.setLoopEnabled(true)
            } else {
                binding.lvTopOffers.visibility = View.GONE
                binding.tvTopOfferNoData.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNormalOffers() {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("skip", startPage)
                requestObject.addProperty("sortByPrice", 1)
                requestObject.addProperty("packageType", "")
                requestObject.addProperty("search", searchQuery)
                requestObject.addProperty("view", "")
                Log.e("Normal request", requestObject.toString())
                val offersViewModel = OffersViewModel()
                offersViewModel.getNormalOffers(
                    requestObject,
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(viewLifecycleOwner, Observer<JSONObject?> { jsonObject ->
                    dismissProgressBar()
                    parseNormalOffersResponse(jsonObject)
                })
            } else {
                CommonClass.showToastMessage(
                    requireActivity(),
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
            Log.e("NormalOffers", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        serviceFlag = false;
                        searchQueryFlag = false;
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
                populateNormalOfferList()
            } else {
                binding.lvNormalOffers.visibility = View.GONE
                binding.tvNormalOfferNoData.visibility = View.VISIBLE
                CommonClass.handleErrorResponse(requireActivity(), obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateNormalOfferList() {
        try {
            binding.lnNormalOffers.visibility = View.VISIBLE
            if (!normalOfferList.isNullOrEmpty()) {
                binding.lvNormalOffers.visibility = View.VISIBLE
                binding.tvNormalOfferNoData.visibility = View.GONE
                linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.lvNormalOffers.layoutManager = linearLayoutManager
                normalOffersListAdapter =
                    NormalOffersListAdapter(requireActivity(), normalOfferList, this@OffersFragment)
                binding.lvNormalOffers.adapter = normalOffersListAdapter
            } else {
                binding.lvNormalOffers.visibility = View.GONE
                binding.tvNormalOfferNoData.visibility = View.VISIBLE
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

    private fun getCartCount() {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty(
                    "userId",
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                )

                val offersViewModel = OffersViewModel()
                offersViewModel.getCartCount(
                    requestObject,
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(viewLifecycleOwner, { jsonObject ->
                    parseCartCountResponse(jsonObject)
                })
            } else {
                CommonClass.showToastMessage(
                    requireActivity(),
                    binding.rootView,
                    resources.getString(R.string.no_internet),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseCartCountResponse(obj: JSONObject) {
        try {
            Log.e("CartCount", obj.toString())
            if (obj.has("status") && obj.getBoolean("status")) {
                if (obj.has("data") && !obj.isNull("data")) {
                    binding.ibvCart.badgeValue = obj.getInt("data")
                }
            } else {
                CommonClass.showToastMessage(
                    requireActivity(),
                    binding.rootView,
                    obj.getString("message"),
                    Toast.LENGTH_SHORT
                );
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProgressBar() {
        try {
            binding.lnProgressbar.root.visibility = View.VISIBLE
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun dismissProgressBar() {
        try {
            if (activity != null) {
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
            binding.lnProgressbar.root.visibility = View.GONE
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun selectTopOfferItem(itemDto: NormalOfferDto) {
        try {
            launchItemDetailsActivity(itemDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectNormalOfferItem(itemDto: NormalOfferDto) {
        try {
            launchItemDetailsActivity(itemDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                callApi()
            }
        }

    private fun launchItemDetailsActivity(itemDto: NormalOfferDto) {
        try {
            val intent = Intent(context, ItemDetailsActivity::class.java)
            intent.putExtra("itemDto", itemDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            resultLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}