package com.illagu.attil.home.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.illagu.attil.R
import com.illagu.attil.databinding.FragmentProductBinding
import com.illagu.attil.home.product.childfrag.PackedFragment
import com.illagu.attil.home.product.childfrag.ProductSubFragment
import com.illagu.attil.listener.BrandSelectListener
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.PreferenceHelper


class ProductFragment : Fragment(), BrandSelectListener {

    private val binding by lazy { FragmentProductBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null

    private var currentFragment = 0

    private val productSubFragment = ProductSubFragment()
    private val packedFragment = PackedFragment()

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
        selectItem(AppConstant.PRODUCT_FRAGMENT)
        handleUiElement()
    }

    private fun handleUiElement() {
        try {
            binding.rgType.setOnPositionChangedListener {
                if (it == 0) {
                    if (currentFragment != AppConstant.PRODUCT_FRAGMENT) selectItem(AppConstant.PRODUCT_FRAGMENT)
                } else {
                    if (currentFragment != AppConstant.PACKED_FRAGMENT) selectItem(AppConstant.PACKED_FRAGMENT)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectItem(fragment: Int) {
        try {
            currentFragment = fragment
            when (fragment) {
                AppConstant.PRODUCT_FRAGMENT -> {
                    launchProductSubFragment()
                }
                AppConstant.PACKED_FRAGMENT -> {
                    launchPackedFragment()
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchProductSubFragment() {
        try {
            val fragmentManager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, productSubFragment)
            transaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchPackedFragment() {
        try {
            val fragmentManager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, packedFragment)
            transaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSelection() {
        try {
            val fragmentManager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, packedFragment)
            transaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}