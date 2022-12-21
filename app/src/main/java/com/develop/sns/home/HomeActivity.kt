package com.develop.sns.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityHomeBinding
import com.develop.sns.home.offers.OffersFragment
import com.develop.sns.home.orders.OrdersFragment
import com.develop.sns.home.product.ProductFragment
import com.develop.sns.home.profile.fragment.ProfileFragment
import com.develop.sns.utils.AppConstant


class HomeActivity : SubModuleActivity() {

    private val TAG = HomeActivity::class.java.simpleName
    private val context: HomeActivity = this@HomeActivity

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private var currentFragment = 0
    private val offersFragment = OffersFragment()
    private val productFragment = ProductFragment()
    private val ordersFragment = OrdersFragment()
    private val profileFragment = ProfileFragment()
    var fa: Activity? = null
    var firstTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fa = this

        selectItem(AppConstant.OFFERS_FRAGMENT)
        initClassReference()
        handleUiElement()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("custom-event-name")
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            ordersReceiver,
            IntentFilter("nav-orders")
        )
    }

    private fun initClassReference() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnOffers.setOnClickListener {
                if (currentFragment != AppConstant.OFFERS_FRAGMENT) selectItem(AppConstant.OFFERS_FRAGMENT)
            }

            binding.lnProducts.setOnClickListener {
                if (currentFragment != AppConstant.PRODUCTS_FRAGMENT) selectItem(AppConstant.PRODUCTS_FRAGMENT)
            }

            binding.lnOrders.setOnClickListener {
                if (currentFragment != AppConstant.ORDERS_FRAGMENT) selectItem(AppConstant.ORDERS_FRAGMENT)
            }

            binding.lnProfile.setOnClickListener {
                if (currentFragment != AppConstant.PROFILE_FRAGMENT) selectItem(AppConstant.PROFILE_FRAGMENT)
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun selectItem(fragment: Int) {
        try {
            currentFragment = fragment
            when (fragment) {
                AppConstant.OFFERS_FRAGMENT -> {
                    binding.tvOffers.setTextColor(ContextCompat.getColor(context, R.color.primary))
                    binding.ivOffers.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvProducts.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivProducts.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvOrders.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivOrders.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvProfile.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivProfile.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    launchOffersFragment()
                }
                AppConstant.PRODUCTS_FRAGMENT -> {
                    binding.tvOffers.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivOffers.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvProducts.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        )
                    )
                    binding.ivProducts.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvOrders.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivOrders.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvProfile.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivProfile.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    launchProductFragment()
                }
                AppConstant.ORDERS_FRAGMENT -> {
                    binding.tvOffers.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivOffers.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvProducts.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivProducts.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvOrders.setTextColor(ContextCompat.getColor(context, R.color.primary))
                    binding.ivOrders.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvProfile.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivProfile.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    launchOrdersFragment()
                }
                AppConstant.PROFILE_FRAGMENT -> {
                    binding.tvOffers.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivOffers.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvProducts.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivProducts.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvOrders.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    binding.ivOrders.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.tvProfile.setTextColor(ContextCompat.getColor(context, R.color.primary))
                    binding.ivProfile.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                    launchProfileFragment()
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchOffersFragment() {
        try {
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, offersFragment)
            transaction.commitAllowingStateLoss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchProductFragment() {
        try {
            firstTime = false
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, productFragment)
            transaction.commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchOrdersFragment() {
        try {
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, ordersFragment)
            transaction.commitAllowingStateLoss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchProfileFragment() {
        try {
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, profileFragment)
            transaction.commitAllowingStateLoss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val cartCount = intent.getIntExtra("cartCount", 0)
            binding.ivOffers.badgeValue = cartCount
        }
    }

    private val ordersReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.e("Comes", "NavOrders")
            if (currentFragment != AppConstant.ORDERS_FRAGMENT) selectItem(AppConstant.ORDERS_FRAGMENT)
        }
    }
}