package com.develop.sns.home.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityVarietyListBinding
import com.develop.sns.home.product.adapter.CategoryVarietyListAdapter
import com.develop.sns.home.product.listener.CategoryProductListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.google.gson.JsonObject
import com.talentmicro.icanrefer.dto.CategoryProductDto
import org.json.JSONObject

class VarietyListActivity : SubModuleActivity(), CategoryProductListener {

    private val context: Context = this@VarietyListActivity
    private val binding by lazy { ActivityVarietyListBinding.inflate(layoutInflater) }

    private lateinit var categoryProductDto: CategoryProductDto

    private lateinit var productVaritiesList: ArrayList<CategoryProductDto>
    private lateinit var categoryVarietyListAdapter: CategoryVarietyListAdapter

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

    private lateinit var linearLayoutManager: GridLayoutManager

    var fa: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fa = this;

        initialiseProgressBar(binding.lnProgressbar)
        initialiseErrorMessage(binding.lnError)
        initToolBar()
        initClassReference()
        getIntentValue()
        handleUiElement()
    }

    private fun initToolBar() {
        try {
            binding.lnToolbar.toolbar.title = resources.getString(R.string.varirities_arrow)
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
            productVaritiesList = ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            categoryProductDto =
                intent.getSerializableExtra("categoryMainDto") as CategoryProductDto;
            getProductByVariety();
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
                        getProductByVariety();
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
            productVaritiesList = ArrayList()
            productVaritiesList.clear()
            Log.e("ResetSize", productVaritiesList.size.toString())
            categoryVarietyListAdapter.notifyDataSetChanged()
            getProductByVariety()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getProductByVariety() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty("skip", startPage)
                requestObject.addProperty("search", searchQuery)
                requestObject.addProperty("commonProductId", categoryProductDto.commonProductId)
                Log.e("VarietyRequestObj", requestObject.toString())
                val productsViewModel = ProductsViewModel()
                productsViewModel.getProductFromVariety(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                )
                    .observe(this, { jsonObject ->
                        if (jsonObject != null) {
                            parseProductVarietyResponse(jsonObject)
                        }
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

    private fun parseProductVarietyResponse(obj: JSONObject) {
        try {
            Log.e("ProductCategory", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        serviceFlag = false
                        searchQueryFlag = false
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val itemObject = dataArray.getJSONObject(i)
                            val categoryProductDto = CategoryProductDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                categoryProductDto.id = itemObject.getString("_id")
                            }

                            if (itemObject.has("commonProductId") && !itemObject.isNull("commonProductId")) {
                                categoryProductDto.commonProductId =
                                    itemObject.getString("commonProductId")
                            }

                            if (itemObject.has("productCode") && !itemObject.isNull("productCode")) {
                                categoryProductDto.productCode =
                                    itemObject.getString("productCode")
                            }

                            if (itemObject.has("commonName") && !itemObject.isNull("commonName")) {
                                categoryProductDto.commonName =
                                    itemObject.getString("commonName")
                            }

                            if (itemObject.has("productName") && !itemObject.isNull("productName")) {
                                categoryProductDto.productName =
                                    itemObject.getString("productName")
                            }

                            if (itemObject.has("productImage") && !itemObject.isNull("productImage")) {
                                categoryProductDto.commonImage =
                                    itemObject.getString("productImage")
                            }

                            if (itemObject.has("brands") && !itemObject.isNull("brands")) {
                                categoryProductDto.brands =
                                    itemObject.getInt("brands")
                            }

                            productVaritiesList.add(categoryProductDto);

                        }
                    }
                }
            }
            populateProductVarietyData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateProductVarietyData() {
        try {
            if (productVaritiesList.isNotEmpty()) {
                binding.lvProductVaritiesList.visibility = View.VISIBLE
                binding.tvProductNoData.visibility = View.GONE

                linearLayoutManager =
                    GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
                binding.lvProductVaritiesList.layoutManager = linearLayoutManager

                val screenWidth = CommonClass.getScreenWidth(context as Activity)
                categoryVarietyListAdapter = CategoryVarietyListAdapter(
                    context,
                    productVaritiesList,
                    this@VarietyListActivity,
                    screenWidth
                )
                binding.lvProductVaritiesList.adapter = categoryVarietyListAdapter
            } else {
                binding.lvProductVaritiesList.visibility = View.GONE
                binding.tvProductNoData.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectCategoryProductItem(categoryMainDto: CategoryProductDto, position: Int) {
        try {
            launchBrandListActivity(categoryMainDto);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchBrandListActivity(categoryMainDto: CategoryProductDto) {
        try {
            val intent = Intent(context, BrandListActivity::class.java)
            intent.putExtra("categoryMainDto", categoryMainDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}