package com.develop.sns.home.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.develop.sns.R
import com.develop.sns.databinding.FragmentOrdersBinding
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import org.json.JSONObject


class OrdersFragment : Fragment() {

    private val binding by lazy { FragmentOrdersBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null

    private lateinit var ordersViewModel: OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClassReference()
        handleUiElement()
        selectItem(AppConstant.ONGOING_ORDERS)
    }

    private fun initClassReference() {
        try {
            ordersViewModel = ViewModelProvider(this)[OrdersViewModel::class.java]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.rgType.setOnPositionChangedListener {
                when (it) {
                    0 -> {
                        binding.tvTitle.text = binding.rgType.getButton(it).text.plus(" ").plus(
                            getString(
                                R.string.orders_dot_title
                            )
                        )
                        selectItem(AppConstant.ONGOING_ORDERS)
                    }
                    1 -> {
                        binding.tvTitle.text = binding.rgType.getButton(it).text.plus(" ").plus(
                            getString(
                                R.string.orders_dot_title
                            )
                        )
                        selectItem(AppConstant.ONGOING_RETURNS)
                    }
                    2 -> {
                        binding.tvTitle.text = binding.rgType.getButton(it).text.plus(" ").plus(
                            getString(
                                R.string.orders_dot_title
                            )
                        )
                        selectItem(AppConstant.DELIVERED)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectItem(item: Int) {
        try {
            when (item) {
                AppConstant.ONGOING_ORDERS -> {
                    callService()
                }
                AppConstant.ONGOING_RETURNS -> {

                }
                AppConstant.DELIVERED -> {

                }
                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callService() {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty(
                    "userId",
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_ID)
                )
                requestObject.addProperty("skip", 1)

                showProgressBar()

                ordersViewModel.getOnGoingOrders(
                    requestObject,
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                ).observe(viewLifecycleOwner) { jsonObject ->
                    dismissProgressBar()
                    parseOnGoingOrders(jsonObject)
                }
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

    private fun parseOnGoingOrders(obj: JSONObject) {
        try {
            Log.e("OnGoing", obj.toString())
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