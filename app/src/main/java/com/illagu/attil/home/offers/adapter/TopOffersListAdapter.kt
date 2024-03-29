package com.illagu.attil.home.offers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.illagu.attil.R
import com.illagu.attil.databinding.TopOfferListItemTmplBinding
import com.illagu.attil.home.offers.dto.ProductDto
import com.illagu.attil.home.offers.listener.TopOfferListener
import com.illagu.attil.utils.PreferenceHelper
import com.squareup.picasso.Picasso


class TopOffersListAdapter(
    val context: Context,
    val items: ArrayList<ProductDto>?,
    val topOfferListener: TopOfferListener,
    val screenWidth: Int,
) : RecyclerView.Adapter<TopOffersListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TopOfferListItemTmplBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items?.size!!
    //override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items?.get(position)!!, position)

    inner class ViewHolder(val binding: TopOfferListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductDto, position: Int) {
            with(binding) {

                val tileWidth = (screenWidth - 32) / 2
                cvMain.requestLayout()
                cvMain.layoutParams.width = tileWidth

                when {
                    position % 4 == 0 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_pink_50
                            )
                        )
                        ivProduct.roundedCornerColor =
                            ContextCompat.getColor(context, R.color.md_pink_50)
                    }
                    position % 4 == 1 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_deep_orange_50
                            )
                        )
                        ivProduct.roundedCornerColor =
                            ContextCompat.getColor(context, R.color.md_deep_orange_50)
                    }
                    position % 4 == 2 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_lime_50
                            )
                        )
                        ivProduct.roundedCornerColor =
                            ContextCompat.getColor(context, R.color.md_lime_50)
                    }
                    position % 4 == 3 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_green_50
                            )
                        )
                        ivProduct.roundedCornerColor =
                            ContextCompat.getColor(context, R.color.md_green_50)
                    }
                }

                tvProductName.text = item.productName

                for (j in 0 until item.brandImage.size) {
                    Picasso.with(context).load(item.brandImage[j])
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product).fit()
                        .into(ivProduct)
                }

                measureText = item.priceDetails[0].unit.toString().plus(" ")
                    .plus(item.priceDetails[0].measureType).plus(" @ ")
                    .plus(context.getString(R.string.Rs)).plus("")
                    .plus(item.priceDetails[0].attilPrice)
                tvMeasure.text = measureText

                lnOfferPercentage.visibility = View.VISIBLE
                tvOfferPercentage.text =
                    item.priceDetails.get(0).offerPercentage.toString().plus("% OFF")

                lnMain.setOnClickListener {
                    topOfferListener.selectTopOfferItem(item)
                }

            }
        }
    }
}