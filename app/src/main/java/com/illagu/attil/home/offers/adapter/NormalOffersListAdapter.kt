package com.illagu.attil.home.offers.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.illagu.attil.R
import com.illagu.attil.databinding.NormalOfferListItemTmplBinding
import com.illagu.attil.home.offers.dto.ProductDto
import com.illagu.attil.home.offers.dto.ProductPriceDto
import com.illagu.attil.home.offers.listener.NormalOfferListener
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import org.json.JSONArray


class NormalOffersListAdapter(
    val context: Context,
    val items: ArrayList<ProductDto>?,
    val normalOfferListener: NormalOfferListener,
) : RecyclerView.Adapter<NormalOffersListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""
    var mrp = 0.0
    var offerMrp = 0.0
    var minUnit = 0.0
    var diff = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NormalOfferListItemTmplBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items?.get(position)!!, position)

    inner class ViewHolder(val binding: NormalOfferListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductDto, position: Int) {
            with(binding) {

                when {
                    position % 4 == 0 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_green_50
                            )
                        )
                    }
                    position % 4 == 1 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_lime_50
                            )
                        )
                    }
                    position % 4 == 2 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_deep_orange_50
                            )
                        )
                    }
                    position % 4 == 3 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_pink_50
                            )
                        )
                    }
                }

                tvProductName.text = item.productName
                tvBrandName.text = item.brandName

                for (j in 0 until item.sliderImage.size) {
                    Picasso.with(context).load(item.sliderImage[j])
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(ivProduct)
                }

                var productPriceDto: ProductPriceDto?

                val obj =
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_MIN_UNITS)

                val jsonArray = JSONArray(obj)

                if (item.packageType.equals("loose") && item.offerType.equals("normal")) {

                    productPriceDto = item.priceDetails.get(0)

                    measureText = context.getString(R.string.starts).plus(" @")
                        .plus(productPriceDto.minUnit).plus(" ")
                        .plus(productPriceDto.minUnitMeasureType)


                    minUnit = productPriceDto.minUnit.toDouble()
                    if (!userexists(jsonArray, productPriceDto.minUnitMeasureType)) {
                        minUnit = (productPriceDto.minUnit * 1000).toDouble()
                    }

                    val unit: Double = productPriceDto.unit.toDouble()
                    val result: Double = minUnit.div(unit)
                    //Log.e("Double result", result.toString())
                    val normalPrice = productPriceDto.normalPrice
                    mrp = result.times(normalPrice)

                    val offerPrice = productPriceDto.attilPrice
                    offerMrp = result.times(offerPrice)

                    lnOfferPercentage.visibility = View.VISIBLE
                    lnBogeMain.visibility = View.GONE
                    ivBogo.visibility = View.GONE

                    tvOfferPercentage.text =
                        productPriceDto.offerPercentage.toString().plus("% OFF")

                    diff = (mrp - offerMrp)


                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))
                } else if (item.packageType.equals("packed") && item.offerType.equals("normal")) {

                    measureText = context.getString(R.string.starts).plus(" @")
                        .plus(item.priceDetails.get(0).unit).plus(" ")
                        .plus(item.priceDetails.get(0).measureType)

                    mrp = item.priceDetails.get(0).normalPrice.toDouble()
                    offerMrp = item.priceDetails.get(0).attilPrice.toDouble()

                    lnOfferPercentage.visibility = View.VISIBLE
                    lnBogeMain.visibility = View.GONE
                    ivBogo.visibility = View.GONE

                    tvOfferPercentage.text =
                        item.priceDetails.get(0).offerPercentage.toString().plus("-")
                            .plus(item.priceDetails.get(item.priceDetails.size - 1).offerPercentage)
                            .plus("% OFF")
                    diff = (mrp - offerMrp)

                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))

                } else if (item.packageType.equals("packed") && item.offerType.equals(
                        "BOGO",
                        false
                    )
                ) {
                    productPriceDto = item.priceDetails.get(0)
                    measureText = context.getString(R.string.starts).plus(" @")
                        .plus(productPriceDto.unit).plus(" ")
                        .plus(productPriceDto.measureType)

                    mrp = productPriceDto.normalPrice.toDouble()
                    offerMrp = productPriceDto.attilPrice.toDouble()

                    lnOfferPercentage.visibility = View.GONE
                    lnBogeMain.visibility = View.GONE
                    ivBogo.visibility = View.VISIBLE

                    diff = (mrp - offerMrp)

                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))
                } else if (item.packageType.equals("packed") && item.offerType.equals(
                        "BOGE",
                        false
                    )
                ) {
                    productPriceDto = item.priceDetails.get(0)
                    measureText = context.getString(R.string.starts).plus(" @")
                        .plus(productPriceDto.unit).plus(" ")
                        .plus(productPriceDto.measureType)

                    mrp = productPriceDto.normalPrice.toDouble()
                    offerMrp = productPriceDto.attilPrice.toDouble()

                    diff = (mrp - offerMrp)

                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))

                    lnOfferPercentage.visibility = View.GONE
                    lnBogeMain.visibility = View.VISIBLE
                    ivBogo.visibility = View.GONE

                    Picasso.with(context).load(productPriceDto.bogeProductImg)
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(ivBogeBrand)
                    tvBogeName.text = productPriceDto.bogeProductName
                    tvBogeQty.text = productPriceDto.bogeUnit.toString().plus(" ")
                        .plus(productPriceDto.bogeMeasureType)
                }

                lnMain.setOnClickListener {
                    val itemDto: ProductDto = items!!.get(position)
                    normalOfferListener.selectNormalOfferItem(itemDto)
                }

            }
        }

    }

    private fun userexists(jsonArray: JSONArray, usernameToFind: String): Boolean {
        return jsonArray.toString().contains("\"$usernameToFind\"")
    }
}