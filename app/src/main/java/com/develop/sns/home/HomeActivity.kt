package com.develop.sns.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityHomeBinding
import com.develop.sns.home.offers.OffersFragment
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
    }

    private fun initClassReference() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnOffers.setOnClickListener(View.OnClickListener {
                if (currentFragment != AppConstant.OFFERS_FRAGMENT) selectItem(AppConstant.OFFERS_FRAGMENT)
            })

            binding.lnProducts.setOnClickListener(View.OnClickListener {
                if (currentFragment != AppConstant.PRODUCTS_FRAGMENT) selectItem(AppConstant.PRODUCTS_FRAGMENT)
            })

            binding.lnOrders.setOnClickListener(View.OnClickListener {
                if (currentFragment != AppConstant.ORDERS_FRAGMENT) selectItem(AppConstant.ORDERS_FRAGMENT)
            })

            binding.lnProfile.setOnClickListener(View.OnClickListener {
                if (currentFragment != AppConstant.PROFILE_FRAGMENT) selectItem(AppConstant.PROFILE_FRAGMENT)
            })

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

                    //launchChatFragment()
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
            // Get extra data included in the Intent
            val cartCount = intent.getIntExtra("cartCount", 0)
            //Log.d("receiver", "Got message: $cartCount")
            binding.ivOffers.badgeValue = cartCount
        }
    }
}