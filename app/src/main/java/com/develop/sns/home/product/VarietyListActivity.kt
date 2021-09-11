package com.develop.sns.home.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

    lateinit var categoryProductDto: CategoryProductDto

    private lateinit var productVaritiesList: ArrayList<CategoryProductDto>
    private lateinit var categoryVarietyListAdapter: CategoryVarietyListAdapter

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

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIntentValue() {
        try {
            val intent = intent
            categoryProductDto =
                intent.getSerializableExtra("categoryMainDto") as CategoryProductDto;
            getProductByVariety(categoryProductDto);
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

    private fun getProductByVariety(categoryProductDto: CategoryProductDto) {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                val requestObject = JsonObject()
                requestObject.addProperty("skip", 0)
                requestObject.addProperty("search", "")
                requestObject.addProperty("commonProductId", categoryProductDto.commonProductId)
                Log.e("RequestObj", requestObject.toString())
                val productsViewModel = ProductsViewModel()
                productsViewModel.getProductFromVariety(requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!)
                    .observe(this, { jsonObject ->
                        if (jsonObject != null) {
                            parseProductByCategoryResponse(jsonObject)
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

    private fun parseProductByCategoryResponse(obj: JSONObject) {
        try {
            Log.e("ProductCategory", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                if (obj.has("status") && obj.getBoolean("status")) {
                    productVaritiesList = ArrayList()
                    if (obj.has("data") && !obj.isNull("data")) {
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
                    populateProductVarietyData()
                } else {
                    binding.lvProductVaritiesList.visibility = View.GONE
                    binding.tvProductNoData.visibility = View.VISIBLE
                    CommonClass.showToastMessage(
                        context,
                        binding.rootView,
                        obj.getString("message"),
                        Toast.LENGTH_SHORT
                    );
                }
            } else {
                binding.lvProductVaritiesList.visibility = View.GONE
                binding.tvProductNoData.visibility = View.VISIBLE
                CommonClass.handleErrorResponse(context, obj, binding.rootView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateProductVarietyData() {
        try {
            if (productVaritiesList.isNotEmpty()) {
                binding.lvProductVaritiesList.visibility = View.VISIBLE
                binding.tvProductNoData.visibility = View.GONE

                val gridLayoutManager =
                    GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
                binding.lvProductVaritiesList.layoutManager = gridLayoutManager

                GridLayoutManager(
                    this, // context
                    2, // span count
                    RecyclerView.VERTICAL, // orientation
                    false // reverse layout
                ).apply {
                    // specify the layout manager for recycler view
                    binding.lvProductVaritiesList.layoutManager = this
                }

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