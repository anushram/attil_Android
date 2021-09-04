package com.develop.sns.home.product.childfrag

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.databinding.FragmentSubProductBinding
import com.develop.sns.home.product.ProductFragment
import com.develop.sns.home.product.ProductsViewModel
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
    private var categoryProductList = ArrayList<CategoryProductDto>()

    private lateinit var categoryMainListAdapter: CategoryMainListAdapter
    private lateinit var categoryProductListAdapter: CategoryProductListAdapter

    private val packedFragment = PackedFragment()

    lateinit var mBrandSelectionSetListener: BrandSelectListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
        onAttachToParentFragment(getParentFragment()!!);
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

    private fun checkForData() {
        try {
            val data = preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_PRODUCTS_OBJ)
            Log.e("data", data!!)
            if (data.isNotEmpty()) {
                val dataObject = JSONObject(data)
                parseCategoryResponse(dataObject)
            } else {
                getProducts()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getProducts() {
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
                    categoryMainList = ArrayList<CategoryMainDto>();
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
                            categoryMainList.add(categoryDto);
                        }
                    }
                    populateCategoryData()
                } else {
                    CommonClass.showToastMessage(
                        requireActivity(),
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    );
                }
            } else {
                var statusCode = obj.getInt("statusCode")
                if (statusCode == 401) {
                    CommonClass.logoutSession(requireActivity());
                    requireActivity().finish();
                } else {
                    CommonClass.showToastMessage(
                        requireActivity(),
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    )
                }
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
                getCategoryItems(categoryMainList[0])
            } else {
                binding.lvCategoryMainList.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectCategoryMainItem(categoryMainDto: CategoryMainDto, position: Int) {
        try {
            for (i in 0 until categoryMainList.size) {
                val categoryMainDto1: CategoryMainDto = categoryMainList.get(i)
                categoryMainDto1.isSelected = false
                categoryMainList.set(i, categoryMainDto1)
            }
            val selectedCategoryDto = categoryMainList.get(position)
            selectedCategoryDto.isSelected = true
            categoryMainList.set(position, selectedCategoryDto)
            categoryMainListAdapter.notifyDataSetChanged()
            getCategoryItems(categoryMainDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCategoryItems(categoryMainDto: CategoryMainDto) {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("skip", 0)
                requestObject.addProperty("sortByPrice", 0)
                requestObject.addProperty("packageType", "")
                requestObject.addProperty("search", "")
                requestObject.addProperty("categoryId", categoryMainDto.id)
                Log.e("RequestObj", requestObject.toString())
                val productsViewModel = ProductsViewModel()
                productsViewModel.getProductFromCategory(requestObject,
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!)
                    .observe(viewLifecycleOwner, { jsonObject ->
                        if (jsonObject != null) {
                            parseProductByCategoryResponse(jsonObject)
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

    private fun parseProductByCategoryResponse(obj: JSONObject) {
        try {
            categoryProductList.clear()
            Log.e("ProductCategory", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
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

                            categoryProductList.add(categoryProductDto);

                        }
                    }
                    populateCategoryProductData()
                } else {
                    binding.lvProductList.visibility = View.GONE
                    binding.tvProductNoData.visibility = View.VISIBLE
                    CommonClass.showToastMessage(
                        requireActivity(),
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    );
                }
            } else {
                binding.lvProductList.visibility = View.GONE
                binding.tvProductNoData.visibility = View.VISIBLE
                val statusCode = obj.getInt("statusCode")
                if (statusCode == 401) {
                    CommonClass.logoutSession(requireActivity());
                    requireActivity().finish();
                } else {
                    CommonClass.showToastMessage(
                        requireActivity(),
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateCategoryProductData() {
        try {
            if (categoryProductList.isNotEmpty()) {
                binding.lvProductList.visibility = View.VISIBLE
                binding.tvProductNoData.visibility = View.GONE

                val gridLayoutManager =
                    GridLayoutManager(requireActivity(), 2, LinearLayoutManager.HORIZONTAL, false)
                binding.lvProductList.layoutManager = gridLayoutManager

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

    override fun selectCategoryProductItem(categoryMainDto: CategoryProductDto, position: Int) {
        try {
            mBrandSelectionSetListener.onSelection()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onAttachToParentFragment(fragment: Fragment) {
        try {
            mBrandSelectionSetListener = fragment as BrandSelectListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$fragment must implement OnPlayerSelectionSetListener")
        }
    }

}