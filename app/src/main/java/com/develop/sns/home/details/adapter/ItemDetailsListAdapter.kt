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
import com.develop.sns.home.offers.dto.NormalOfferDto
import com.develop.sns.home.offers.dto.NormalOfferPriceDto
import com.develop.sns.home.offers.listener.ItemListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import org.json.JSONArray


class ItemDetailsListAdapter(
    val context: Context,
    val normalOfferDto: NormalOfferDto,
    private val priceDetailsList: ArrayList<NormalOfferPriceDto>?,
    val itemListener: ItemListener,
) : RecyclerView.Adapter<ItemDetailsListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""
    var mrp = 0.0
    var offerMrp = 0.0
    var minUnit = 0.0
    var diff = 0.0

    companion object {
        var clickGmPlusCount = 0
    }

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
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_MIN_UNITS)

                val jsonArray = JSONArray(obj)

                Picasso.with(context).load(normalOfferDto.brandImage!![0])
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product)
                    .into(ivProduct)
                Log.e("PackageType", normalOfferDto.packageType!!)
                Log.e("OfferType", normalOfferDto.offerType!!)
                if (normalOfferDto.packageType.equals("loose", true)
                    && normalOfferDto.offerType.equals("normal", true)
                ) {

                    measureText = context.getString(R.string.available).plus(" ")
                        .plus(priceDetailDto.maxUnit).plus(" ")
                        .plus(priceDetailDto.maxUnitMeasureType)


                    minUnit = priceDetailDto.minUnit.toDouble()
                    if (!dataExists(jsonArray, priceDetailDto.minUnitMeasureType)) {
                        minUnit = priceDetailDto.minUnit * 1000.toDouble()
                    }
                    val result: Double = minUnit / priceDetailDto.unit.toDouble()
                    val normalPrice = priceDetailDto.normalPrice
                    mrp = result.times(normalPrice)

                    val offerPrice = priceDetailDto.attilPrice
                    offerMrp = result.times(offerPrice)

                    lnOfferPercentage.visibility = View.VISIBLE
                    tvOfferPercentage.text =
                        priceDetailDto.offerPercentage.toString().plus("% OFF")
                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    diff = (mrp - offerMrp)
                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))

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

                    mrp = priceDetailDto.normalPrice.toDouble()
                    offerMrp = priceDetailDto.attilPrice.toDouble()

                    lnOfferPercentage.visibility = View.VISIBLE
                    tvOfferPercentage.text =
                        priceDetailDto.offerPercentage.toString().plus(" ").plus("% OFF")

                    tvMeasure.text = measureText

                    tvMrp.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    diff = (mrp - offerMrp)
                    tvSave.text =
                        context.getString(R.string.you_save).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))

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

                    mrp = priceDetailDto.normalPrice.toDouble()
                    offerMrp = priceDetailDto.attilPrice.toDouble()

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
                } else if (normalOfferDto.packageType.equals("packed", true)
                    && normalOfferDto.offerType.equals("BOGE", true)
                ) {
                    measureText = priceDetailDto.unit.toString().plus(" ")
                        .plus(priceDetailDto.measureType)

                    mrp = priceDetailDto.normalPrice.toDouble()
                    offerMrp = priceDetailDto.attilPrice.toDouble()

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

                    Picasso.with(context).load(priceDetailDto.bogeProductImg)
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(ivBogeBrand)
                    tvBogeName.text = priceDetailDto.bogeProductName
                    tvBogeQty.text = priceDetailDto.bogeUnit.toString().plus(" ")
                        .plus(priceDetailDto.bogeMeasureType)
                }

                if (normalOfferDto.packageType.equals("loose", true)
                    && normalOfferDto.offerType.equals("normal", true)
                ) {
                    Log.e("Quantity", priceDetailDto.quantity.toString())
                    btnAdd.visibility = View.GONE
                    lnLooseAdd.visibility = View.VISIBLE
                    lnAdd.visibility = View.GONE
                    val kg = priceDetailDto.quantity.toDouble() * 0.001
                    var minUnit = "0"
                    var maxUnit = "0"
                    val qtyStr = "%.3f".format(kg)
                    minUnit = qtyStr.split(".")[1]
                    maxUnit = qtyStr.split(".")[0]
                    Log.e("min", minUnit)
                    Log.e("max", maxUnit)
                    if (minUnit.equals("000")) {
                        minUnit = "0"
                    }
                    tvGmCount.text = minUnit
                    tvKgCount.text = maxUnit
                } else {
                    if (priceDetailDto.quantity > 0) {
                        btnAdd.visibility = View.GONE
                        lnLooseAdd.visibility = View.GONE
                        lnAdd.visibility = View.VISIBLE
                        tvCount.text = priceDetailDto.quantity.toString()
                    } else {
                        btnAdd.visibility = View.VISIBLE
                        lnAdd.visibility = View.GONE
                        lnLooseAdd.visibility = View.GONE
                    }
                }

                btnAdd.setOnClickListener {
                    btnAdd.visibility = View.GONE
                    if (normalOfferDto.packageType.equals("loose", true)
                        && normalOfferDto.offerType.equals("normal", true)
                    ) {
                        lnLooseAdd.visibility = View.VISIBLE
                        lnAdd.visibility = View.GONE
                    } else {
                        lnLooseAdd.visibility = View.GONE
                        lnAdd.visibility = View.VISIBLE
                    }
                    itemListener.selectItem(position, priceDetailDto, true)
                }

                lnIncrease.setOnClickListener {
                    itemListener.changeCount(position, priceDetailDto, true)
                }

                lnDecrease.setOnClickListener {
                    itemListener.changeCount(position, priceDetailDto, false)
                }

                lnGmIncrease.setOnClickListener {
                    itemListener.changeCountGmOrKg(
                        position, priceDetailDto,
                        isAdd = true,
                        isGm = true,
                        ++clickGmPlusCount
                    )
                }

                lnGmDecrease.setOnClickListener {
                    itemListener.changeCountGmOrKg(position, priceDetailDto,
                        isAdd = false,
                        isGm = true, 0)
                }

                lnKgIncrease.setOnClickListener {
                    itemListener.changeCountGmOrKg(position,
                        priceDetailDto,
                        isAdd = true,
                        isGm = false, 0)
                }

                lnKgDecrease.setOnClickListener {
                    itemListener.changeCountGmOrKg(position,
                        priceDetailDto,
                        isAdd = false,
                        isGm = false, 0)
                }


            }
        }

    }

    private fun dataExists(jsonArray: JSONArray, dataToFind: String): Boolean {
        return jsonArray.toString().contains("\"$dataToFind\"")
    }
}