package com.develop.sns.home.details.adapter

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.databinding.ItemDetailsListItemTmplBinding
import com.develop.sns.home.dto.NormalOfferDto
import com.develop.sns.home.dto.NormalOfferPriceDto
import com.develop.sns.home.offers.listener.ItemListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import org.json.JSONArray


class ItemDetailsListAdapter(
    val context: Context,
    val normalOfferDto: NormalOfferDto,
    private val priceDetailsList: ArrayList<NormalOfferPriceDto>?,
    val itemListener: ItemListener
) : RecyclerView.Adapter<ItemDetailsListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""
    var mrp = 0
    var offerMrp = 0
    var unit = 0
    var diff = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDetailsListItemTmplBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = priceDetailsList?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(priceDetailsList?.get(position)!!, position)

    inner class ViewHolder(val binding: ItemDetailsListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(priceDetailDto: NormalOfferPriceDto, position: Int) {
            with(binding) {
                tvProductName.text = normalOfferDto.productName

                val obj =
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_MIN_UNITS);

                val jsonArray = JSONArray(obj)

                Picasso.with(context).load(normalOfferDto.brandImage!![0])
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product)
                    .into(ivProduct);

                if (normalOfferDto.packageType.equals("loose", true)
                    && normalOfferDto.offerType.equals("normal", true)
                ) {

                    measureText = context.getString(R.string.available).plus(" ")
                        .plus(priceDetailDto.maxUnit).plus(" ")
                        .plus(priceDetailDto.maxUnitMeasureType)


                    unit = priceDetailDto.minUnit!!
                    if (!dataExists(jsonArray, priceDetailDto.minUnitMeasureType!!)) {
                        unit = priceDetailDto.minUnit!! * 1000
                    }
                    val result: Int = unit / priceDetailDto.unit!!
                    val normalPrice = priceDetailDto.normalPrice!!
                    mrp = result.times(normalPrice)

                    val offerPrice = priceDetailDto.attilPrice!!
                    offerMrp = result.times(offerPrice)

                    tvOfferPercentage.visibility = View.VISIBLE
                    tvOfferPercentage.text =
                        priceDetailDto.offerPercentage!!.toString().plus("% OFF")
                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus(mrp)
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus(offerMrp)

                    diff = mrp - offerMrp
                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus(diff)

                    lnBogeMain.visibility = View.GONE
                    ivBogo.visibility = View.GONE
                    tvMrp.visibility = View.VISIBLE
                    tvOfferPrice.visibility = View.VISIBLE
                    tvSave.visibility = View.VISIBLE

                } else if ((normalOfferDto.packageType.equals("packed", true)
                            && normalOfferDto.offerType.equals("normal", true))
                    || (normalOfferDto.packageType.equals("packed", true)
                            && normalOfferDto.offerType.equals("special", true))
                ) {
                    measureText =
                        priceDetailDto.unit.toString().plus(" ").plus(priceDetailDto.measureType)

                    mrp = priceDetailDto.normalPrice!!
                    offerMrp = priceDetailDto.attilPrice!!

                    tvOfferPercentage.visibility = View.VISIBLE
                    tvOfferPercentage.text =
                        priceDetailDto.offerPercentage.toString().plus(" ").plus("% OFF")

                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus(mrp)
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus(offerMrp)

                    diff = mrp - offerMrp
                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus(diff)

                    lnBogeMain.visibility = View.GONE
                    ivBogo.visibility = View.GONE
                    tvMrp.visibility = View.VISIBLE
                    tvOfferPrice.visibility = View.VISIBLE
                    tvSave.visibility = View.VISIBLE

                } else if (normalOfferDto.packageType.equals("packed", true)
                    && normalOfferDto.offerType.equals("BOGO", true)
                ) {
                    measureText = priceDetailDto.unit.toString().plus(" ")
                        .plus(priceDetailDto.measureType)

                    mrp = priceDetailDto.normalPrice!!
                    offerMrp = priceDetailDto.attilPrice!!

                    tvOfferPercentage.visibility = View.GONE
                    lnBogeMain.visibility = View.GONE
                    ivBogo.visibility = View.VISIBLE

                    diff = mrp - offerMrp

                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus(mrp)
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus(offerMrp)

                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus(diff)
                } else if (normalOfferDto.packageType.equals("packed", true)
                    && normalOfferDto.offerType.equals("BOGE", true)
                ) {
                    measureText = priceDetailDto.unit.toString().plus(" ")
                        .plus(priceDetailDto.measureType)

                    mrp = priceDetailDto.normalPrice!!
                    offerMrp = priceDetailDto.attilPrice!!

                    diff = mrp - offerMrp

                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus(mrp)
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus(offerMrp)

                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus(diff)

                    tvOfferPercentage.visibility = View.GONE
                    lnBogeMain.visibility = View.VISIBLE
                    ivBogo.visibility = View.GONE

                    Picasso.with(context).load(priceDetailDto.bogeProductImg)
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(ivBogeBrand);
                    tvBogeName.text = priceDetailDto.bogeProductName
                    tvBogeQty.text = priceDetailDto.bogeUnit.toString().plus(" ")
                        .plus(priceDetailDto.bogeMeasureType)
                }

                if (priceDetailDto.quantity!! > 0) {
                    btnAdd.visibility = View.GONE
                    lnAdd.visibility = View.VISIBLE
                    tvCount.text = priceDetailDto.quantity.toString()
                } else {
                    btnAdd.visibility = View.VISIBLE
                    lnAdd.visibility = View.GONE
                }

                btnAdd.setOnClickListener {
                    btnAdd.visibility = View.GONE
                    lnAdd.visibility = View.VISIBLE
                    itemListener.selectItem(position, priceDetailDto, true)
                }

                lnIncrease.setOnClickListener {
                    itemListener.changeCount(position, priceDetailDto, true);
                }

                lnDecrease.setOnClickListener {
                    itemListener.changeCount(position, priceDetailDto, false);
                }
            }
        }

    }

    private fun dataExists(jsonArray: JSONArray, dataToFind: String): Boolean {
        return jsonArray.toString().contains("\"$dataToFind\"")
    }
}