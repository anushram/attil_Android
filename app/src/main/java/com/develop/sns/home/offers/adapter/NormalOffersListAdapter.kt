package com.develop.sns.home.offers.adapter

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.databinding.NormalOfferListItemTmplBinding
import com.develop.sns.home.dto.NormalOfferDto
import com.develop.sns.home.dto.NormalOfferPriceDto
import com.develop.sns.home.offers.listener.NormalOfferListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import org.json.JSONArray
import kotlin.math.roundToInt


class NormalOffersListAdapter(
    val context: Context,
    val items: ArrayList<NormalOfferDto>?,
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
        fun bind(item: NormalOfferDto, position: Int) {
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

                for (j in 0 until item.brandImage!!.size) {
                    Picasso.with(context).load(item.brandImage!![j])
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(ivProduct);
                }

                var normalOfferPriceDto: NormalOfferPriceDto?

                val obj =
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_MIN_UNITS);

                val jsonArray = JSONArray(obj)

                if (item.packageType.equals("loose") && item.offerType.equals("normal")) {

                    normalOfferPriceDto = item.priceDetails?.get(0)

                    measureText = context.getString(R.string.starts).plus(" @")
                        .plus(normalOfferPriceDto!!.minUnit).plus(" ")
                        .plus(normalOfferPriceDto.minUnitMeasureType)


                    minUnit = normalOfferPriceDto.minUnit!!.toDouble()
                    if (!userexists(jsonArray, normalOfferPriceDto.minUnitMeasureType!!)) {
                        minUnit = (normalOfferPriceDto.minUnit!! * 1000).toDouble()
                    }

                    val unit: Double = normalOfferPriceDto.unit!!.toDouble()
                    val result: Double = minUnit.div(unit)
                    Log.e("Double result", result.toString())
                    val normalPrice = normalOfferPriceDto.normalPrice!!
                    mrp = result.times(normalPrice)

                    val offerPrice = normalOfferPriceDto.attilPrice!!
                    offerMrp = result.times(offerPrice)

                    tvOfferPercentage.visibility = View.VISIBLE
                    lnBogeMain.visibility = View.GONE
                    ivBogo.visibility = View.GONE

                    tvOfferPercentage.text =
                        normalOfferPriceDto.offerPercentage!!.toString().plus("% OFF")

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
                        .plus(item.priceDetails?.get(0)!!.unit).plus(" ")
                        .plus(item.priceDetails?.get(0)!!.measureType)

                    mrp = item.priceDetails?.get(0)!!.normalPrice!!.toDouble()
                    offerMrp = item.priceDetails?.get(0)!!.attilPrice!!.toDouble()

                    tvOfferPercentage.visibility = View.VISIBLE
                    lnBogeMain.visibility = View.GONE
                    ivBogo.visibility = View.GONE

                    tvOfferPercentage.text =
                        item.priceDetails?.get(0)!!.offerPercentage.toString().plus("-")
                            .plus(item.priceDetails?.get(item.priceDetails?.size!! - 1)!!.offerPercentage)
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
                    normalOfferPriceDto = item.priceDetails?.get(0)
                    measureText = context.getString(R.string.starts).plus(" @")
                        .plus(normalOfferPriceDto!!.unit).plus(" ")
                        .plus(normalOfferPriceDto.measureType)

                    mrp = normalOfferPriceDto.normalPrice!!.toDouble()
                    offerMrp = normalOfferPriceDto.attilPrice!!.toDouble()

                    tvOfferPercentage.visibility = View.GONE
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
                    normalOfferPriceDto = item.priceDetails?.get(0)
                    measureText = context.getString(R.string.starts).plus(" @")
                        .plus(normalOfferPriceDto!!.unit).plus(" ")
                        .plus(normalOfferPriceDto.measureType)

                    mrp = normalOfferPriceDto.normalPrice!!.toDouble()
                    offerMrp = normalOfferPriceDto.attilPrice!!.toDouble()

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

                    tvOfferPercentage.visibility = View.GONE
                    lnBogeMain.visibility = View.VISIBLE
                    ivBogo.visibility = View.GONE

                    Picasso.with(context).load(normalOfferPriceDto.bogeProductImg)
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(ivBogeBrand);
                    tvBogeName.text = normalOfferPriceDto.bogeProductName
                    tvBogeQty.text = normalOfferPriceDto.bogeUnit.toString().plus(" ")
                        .plus(normalOfferPriceDto.bogeMeasureType)
                }

                lnMain.setOnClickListener {
                    val itemDto: NormalOfferDto = items!!.get(position)
                    normalOfferListener.selectNormalOfferItem(itemDto)
                }

            }
        }

    }

    private fun userexists(jsonArray: JSONArray, usernameToFind: String): Boolean {
        return jsonArray.toString().contains("\"$usernameToFind\"")
    }
}