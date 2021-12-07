package com.develop.sns.home.product.childfrag

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.databinding.FragmentSubProductBinding
import com.develop.sns.home.product.ProductsViewModel
import com.develop.sns.home.product.VarietyListActivity
import com.develop.sns.home.product.adapter.CategoryMainListAdapter
import com.develop.sns.home.product.adapter.CategoryProductListAdapter
import com.develop.sns.home.product.listener.CategoryMainListener
import com.develop.sns.home.product.listener.CategoryProductListener
import com.develop.sns.listener.BrandSelectListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import com.talentmicro.icanrefer.dto.CategoryMainDto
import com.talentmicro.icanrefer.dto.CategoryProductDto
import org.json.JSONObject


class ProductSubFragment : Fragment(), CategoryMainListener, CategoryProductListener {

    private val binding by lazy { FragmentSubProductBinding.inflate(layoutInflater) }
    private lateinit var preferenceHelper: PreferenceHelper

    private lateinit var language: String
    private lateinit var categoryMainList: ArrayList<CategoryMainDto>
    private lateinit var categoryProductList: ArrayList<CategoryProductDto>

    private lateinit var categoryMainListAdapter: CategoryMainListAdapter
    private lateinit var categoryProductListAdapter: CategoryProductListAdapter

    lateinit var mBrandSelectionSetListener: BrandSelectListener

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

    private lateinit var selectedCategoryDto: CategoryMainDto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
        onAttachToParentFragment(requireParentFragment())
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
        checkForData()
        initClassReference()
        handleUiElement()
    }

    private fun initClassReference() {
        try {
            language = preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_LANGUAGE)!!
            categoryProductList = ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideKeyboard() {
        try {
            val view = requireActivity().currentFocus
            if (view != null) {
                (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        } catch (e: java.lang.Exception) {
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
                    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                    resetPagination()
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isEmpty()) {
                        searchQuery = newText
                        serviceFlag = false
                        binding.svSearch.clearFocus()
                        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                        searchQueryFlag = true
                        resetPagination()
                    }
                    return false
                }
            })

            binding.lvProductList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount: Int = linearLayoutManager.childCount
                    val totalItemCount: Int = linearLayoutManager.itemCount
                    val firstVisibleItemPosition: Int =
                        linearLayoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition: Int =
                        linearLayoutManager.findLastVisibleItemPosition()
                    val rvRect = Rect()
                    binding.lvProductList.getGlobalVisibleRect(rvRect)
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
                        //Log.d("-----", "end")
                        startPage++
                        //Log.e("StartPage", startPage.toString())
                        getCategoryProducts(selectedCategoryDto)
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
            categoryProductList = ArrayList()
            this.categoryProductList.clear()
            //Log.e("ResetSize", categoryProductList.size.toString())
            categoryProductListAdapter.notifyDataSetChanged()
            getCategoryProducts(selectedCategoryDto)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun checkForData() {
        try {
            val data = preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_PRODUCTS_OBJ)
            //Log.e("data", data!!)
            if (data!!.isNotEmpty()) {
                val dataObject = JSONObject(data)
                parseCategoryResponse(dataObject)
            } else {
                getCategories()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCategories() {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                showProgressBar()
                val productsViewModel = ProductsViewModel()
                productsViewModel.getProducts(preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!)
                    .observe(viewLifecycleOwner, { jsonObject ->
                        if (jsonObject != null) {
                            dismissProgressBar()
                            parseCategoryResponse(jsonObject)
                        }
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

    private fun parseCategoryResponse(obj: JSONObject) {
        try {
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    categoryMainList = ArrayList<CategoryMainDto>()
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val itemObject = dataArray.getJSONObject(i)
                            val categoryDto = CategoryMainDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                categoryDto.id = itemObject.getString("_id")
                            }

                            if (itemObject.has("categoryName") && !itemObject.isNull("categoryName")) {
                                categoryDto.categoryName = itemObject.getString("categoryName")
                            }

                            if (itemObject.has("categoryImage") && !itemObject.isNull("categoryImage")) {
                                categoryDto.categoryImage = itemObject.getString("categoryImage")
                            }
                            categoryMainList.add(categoryDto)
                        }
                    }
                    populateCategoryData()
                } else {
                    CommonClass.showToastMessage(
                        requireActivity(),
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    )
                }
            } else {
                CommonClass.handleErrorResponse(requireActivity(), obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateCategoryData() {
        try {
            if (!categoryMainList.isNullOrEmpty()) {
                binding.lvCategoryMainList.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                val gridLayoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                binding.lvCategoryMainList.layoutManager = gridLayoutManager
                val screenWidth = CommonClass.getScreenWidth(requireActivity())
                categoryMainListAdapter = CategoryMainListAdapter(
                    requireActivity(),
                    categoryMainList,
                    this@ProductSubFragment,
                    screenWidth
                )
                binding.lvCategoryMainList.adapter = categoryMainListAdapter
                val categoryMainDto = categoryMainList[0]
                categoryMainDto.isSelected = true
                categoryMainListAdapter.notifyDataSetChanged()
                selectedCategoryDto = categoryMainList[0]
                getCategoryProducts(selectedCategoryDto)
            } else {
                binding.lvCategoryMainList.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectCategoryItem(categoryMainDto: CategoryMainDto, position: Int) {
        try {
            this.selectedCategoryDto = categoryMainDto
            for (i in 0 until categoryMainList.size) {
                val categoryMainDto1: CategoryMainDto = categoryMainList.get(i)
                categoryMainDto1.isSelected = false
                categoryMainList[i] = categoryMainDto1
            }
            selectedCategoryDto.isSelected = true
            categoryMainList[position] = selectedCategoryDto
            categoryMainListAdapter.notifyDataSetChanged()
            getCategoryProducts(selectedCategoryDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCategoryProducts(categoryMainDto: CategoryMainDto?) {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                showProgressBar()
                val requestObject = JsonObject()
                requestObject.addProperty("skip", startPage)
                requestObject.addProperty("sortByPrice", 0)
                requestObject.addProperty("packageType", "")
                requestObject.addProperty("search", searchQuery)
                requestObject.addProperty("categoryId", categoryMainDto!!.id)
                //Log.e("CPRequestObj", requestObject.toString())
                val productsViewModel = ProductsViewModel()
                productsViewModel.getProductFromCategory(
                    requestObject,
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                )
                    .observe(viewLifecycleOwner, { jsonObject ->
                        if (jsonObject != null) {
                            dismissProgressBar()
                            parseCategoryProductsResponse(jsonObject)
                        }
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

    private fun parseCategoryProductsResponse(obj: JSONObject) {
        try {
            categoryProductList.clear()
            //Log.e("ProductCategory", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        serviceFlag = false
                        searchQueryFlag = false
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            //for (i in 0 until 2) {
                            val itemObject = dataArray.getJSONObject(i)
                            val categoryProductDto = CategoryProductDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                categoryProductDto.id = itemObject.getString("_id")
                            }

                            if (itemObject.has("commonName") && !itemObject.isNull("commonName")) {
                                categoryProductDto.commonName =
                                    itemObject.getString("commonName")
                            }

                            if (itemObject.has("commonImage") && !itemObject.isNull("commonImage")) {
                                categoryProductDto.commonImage =
                                    itemObject.getString("commonImage")
                            }

                            if (itemObject.has("commonProductId") && !itemObject.isNull("commonProductId")) {
                                categoryProductDto.commonProductId =
                                    itemObject.getString("commonProductId")
                            }

                            if (itemObject.has("varieties") && !itemObject.isNull("varieties")) {
                                categoryProductDto.varieties =
                                    itemObject.getInt("varieties")
                            }

                            if (itemObject.has("brands") && !itemObject.isNull("brands")) {
                                categoryProductDto.brands =
                                    itemObject.getInt("brands")
                            }

                            categoryProductList.add(categoryProductDto)

                        }
                    }
                }
            }
            populateCategoryProductData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateCategoryProductData() {
        try {
            if (categoryProductList.isNotEmpty()) {
                binding.lvProductList.visibility = View.VISIBLE
                binding.tvProductNoData.visibility = View.GONE

                linearLayoutManager =
                    GridLayoutManager(requireActivity(), 2, RecyclerView.VERTICAL, false)
                binding.lvProductList.layoutManager = linearLayoutManager

                val screenWidth = CommonClass.getScreenWidth(requireActivity())
                categoryProductListAdapter = CategoryProductListAdapter(
                    requireActivity(),
                    categoryProductList,
                    this@ProductSubFragment,
                    screenWidth
                )
                binding.lvProductList.adapter = categoryProductListAdapter
            } else {
                binding.lvProductList.visibility = View.GONE
                binding.tvProductNoData.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectCategoryProductItem(categoryMainDto: CategoryProductDto, position: Int) {
        try {
            launchVarietyActivity(categoryMainDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchVarietyActivity(categoryMainDto: CategoryProductDto) {
        try {
            val intent = Intent(context, VarietyListActivity::class.java)
            intent.putExtra("categoryMainDto", categoryMainDto)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onAttachToParentFragment(fragment: Fragment) {
        try {
            mBrandSelectionSetListener = fragment as BrandSelectListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$fragment must implement OnPlayerSelectionSetListener"
            )
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

}