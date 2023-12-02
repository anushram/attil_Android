package com.illagu.attil.home.product.childfrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.illagu.attil.databinding.FragmentPackedBinding
import com.illagu.attil.utils.PreferenceHelper


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