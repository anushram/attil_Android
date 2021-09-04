package com.develop.sns.home.product.childfrag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.develop.sns.R
import com.develop.sns.customviews.GravitySnapHelper
import com.develop.sns.databinding.FragmentOffersBinding
import com.develop.sns.databinding.FragmentPackedBinding
import com.develop.sns.home.details.ItemDetailsActivity
import com.develop.sns.home.dto.NormalOfferDto
import com.develop.sns.home.dto.NormalOfferPriceDto
import com.develop.sns.home.offers.adapter.NormalOffersListAdapter
import com.develop.sns.home.offers.adapter.TopOffersListAdapter
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


class PackedFragment : Fragment() {

    private val binding by lazy { FragmentPackedBinding.inflate(layoutInflater) }
    private lateinit var preferenceHelper: PreferenceHelper

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
}