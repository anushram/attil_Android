package com.develop.sns.home.offers.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.develop.sns.R
import com.develop.sns.databinding.ImageSliderLayoutBinding
import com.develop.sns.databinding.NormalOfferListItemTmplBinding
import com.develop.sns.home.dto.NormalOfferDto
import com.develop.sns.home.dto.NormalOfferPriceDto
import com.develop.sns.home.offers.adapter.SliderAdapter.SliderAdapterVH
import com.develop.sns.utils.AppConstant
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import org.json.JSONArray
import java.util.*

class SliderAdapter(private val context: Context, private val mSliderItems: ArrayList<String>) :
    SliderViewAdapter<SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ImageSliderLayoutBinding.inflate(inflater, parent, false)
        return SliderAdapterVH(binding)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        viewHolder.bind(mSliderItems.get(position), position)
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    inner class SliderAdapterVH(val binding: ImageSliderLayoutBinding) :
        SliderViewAdapter.ViewHolder(binding.root) {
        fun bind(item: String, position: Int) {
            with(binding) {
                Picasso.with(context).load(item)
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product).fit()
                    .into(ivAutoImageSlider)
            }
        }
    }
}