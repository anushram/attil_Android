package com.develop.sns.home.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.develop.sns.R
import com.develop.sns.databinding.FragmentProductBinding
import com.develop.sns.home.HomeActivity
import com.develop.sns.home.product.ProductsViewModel
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.*


class ProductFragment : Fragment() {

    private val binding by lazy { FragmentProductBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null
    private var productsViewModel: ProductsViewModel = ProductsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity());
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
        Log.e("Products", "Came")
        initClassReference()
        handleUiElement()
        getTopOffers()
    }


    private fun initClassReference() {
        try {

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

    private fun getTopOffers() {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("skip", 0)
                Log.e("requestObj", requestObject.toString())
                if (HomeActivity().firstTime) {
                    showProgressBar()
                }
                productsViewModel.getTopOffers(
                    requestObject,
                    preferenceHelper?.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!)
                    .observe(viewLifecycleOwner, { jsonObject ->
                        if (jsonObject != null) {
                            parseTopOffersResponse(jsonObject)
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

    private fun parseTopOffersResponse(obj: JSONObject) {
        try {
            Log.e("ProductOffers", obj.toString())
            HomeActivity().firstTime = true
            dismissProgressBar()
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

}