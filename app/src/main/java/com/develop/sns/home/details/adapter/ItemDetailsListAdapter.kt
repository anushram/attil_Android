package com.develop.sns.home.details.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.databinding.ItemDetailsListItemTmplBinding
import com.develop.sns.home.offers.dto.ProductDto
import com.develop.sns.home.offers.dto.ProductPriceDto
import com.develop.sns.home.offers.listener.ItemListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import org.json.JSONArray


class ItemDetailsListAdapter(
    val context: Context,
    val productDto: ProductDto,
    private val priceDetailsList: ArrayList<ProductPriceDto>,
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

    override fun getItemCount(): Int = priceDetailsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(priceDetailsList.get(position), position)

    inner class ViewHolder(val binding: ItemDetailsListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(productPriceDto: ProductPriceDto, position: Int) {
            with(binding) {
                tvProductName.text = productDto.productName

                val obj = preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_MIN_UNITS)

                val jsonArray = JSONArray(obj)

                Picasso.with(context).load(productDto.brandImage[0])
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product)
                    .into(ivProduct)

                if (productDto.packageType.equals("loose", true)
                    && productDto.offerType.equals("normal", true)
                ) {

                    measureText = context.getString(R.string.available).plus(" ")
                        .plus(productPriceDto.maxUnit).plus(" ")
                        .plus(productPriceDto.maxUnitMeasureType)


                    minUnit = productPriceDto.minUnit.toDouble()
                    if (!dataExists(jsonArray, productPriceDto.minUnitMeasureType)) {
                        minUnit = productPriceDto.minUnit * 1000.toDouble()
                    }
                    val result: Double = minUnit / productPriceDto.unit.toDouble()
                    val normalPrice = productPriceDto.normalPrice
                    mrp = result.times(normalPrice)

                    val offerPrice = productPriceDto.attilPrice
                    offerMrp = result.times(offerPrice)

                    lnOfferPercentage.visibility = View.VISIBLE
                    tvOfferPercentage.text =
                        productPriceDto.offerPercentage.toString().plus("% OFF")
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

                } else if ((productDto.packageType.equals("packed", true)
                            && productDto.offerType.equals("normal", true))
                    || (productDto.packageType.equals("packed", true)
                            && productDto.offerType.equals("special", true))
                ) {
                    measureText =
                        productPriceDto.unit.toString().plus(" ").plus(productPriceDto.measureType)

                    mrp = productPriceDto.normalPrice.toDouble()
                    offerMrp = productPriceDto.attilPrice.toDouble()

                    lnOfferPercentage.visibility = View.VISIBLE
                    tvOfferPercentage.text =
                        productPriceDto.offerPercentage.toString().plus("% OFF")

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

                } else if (productDto.packageType.equals("packed", true)
                    && productDto.offerType.equals("BOGO", true)
                ) {
                    measureText = productPriceDto.unit.toString().plus(" ")
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
                } else if (productDto.packageType.equals("packed", true)
                    && productDto.offerType.equals("BOGE", true)
                ) {
                    measureText = productPriceDto.unit.toString().plus(" ")
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

                if (productPriceDto.quantity > 0) {
                    if (productDto.packageType.equals("loose", true)
                        && productDto.offerType.equals("normal", true)
                    ) {
                        //Log.e("Quantity", productPriceDto.quantity.toString())
                        btnAdd.visibility = View.GONE
                        lnLooseAdd.visibility = View.VISIBLE
                        lnAdd.visibility = View.GONE
                        val kg = productPriceDto.quantity.toDouble() * 0.001
                        var minUnit = "0"
                        var maxUnit = "0"
                        val qtyStr = "%.3f".format(kg)
                        minUnit = qtyStr.split(".")[1]
                        maxUnit = qtyStr.split(".")[0]
                        //Log.e("min", minUnit)
                        //Log.e("max", maxUnit)
                        if (minUnit.equals("000")) {
                            minUnit = "0"
                        }
                        tvGmCount.text = minUnit
                        tvKgCount.text = maxUnit
                    } else {
                        btnAdd.visibility = View.GONE
                        lnLooseAdd.visibility = View.GONE
                        lnAdd.visibility = View.VISIBLE
                        tvCount.text = productPriceDto.quantity.toString()
                    }
                } else {
                    btnAdd.visibility = View.VISIBLE
                    lnAdd.visibility = View.GONE
                    lnLooseAdd.visibility = View.GONE
                }

                btnAdd.setOnClickListener {
                    btnAdd.visibility = View.GONE
                    if (productDto.packageType.equals("loose", true)
                        && productDto.offerType.equals("normal", true)
                    ) {
                        lnLooseAdd.visibility = View.VISIBLE
                        lnAdd.visibility = View.GONE
                    } else {
                        lnLooseAdd.visibility = View.GONE
                        lnAdd.visibility = View.VISIBLE
                    }
                    itemListener.selectItem(position, productPriceDto, true)
                }

                lnIncrease.setOnClickListener {
                    itemListener.changeCount(position, productPriceDto, true)
                }

                lnDecrease.setOnClickListener {
                    itemListener.changeCount(position, productPriceDto, false)
                }

                lnGmIncrease.setOnClickListener {
                    itemListener.changeCountGmOrKg(
                        position, productPriceDto,
                        isAdd = true,
                        isGm = true,
                        ++clickGmPlusCount
                    )
                }

                lnGmDecrease.setOnClickListener {
                    itemListener.changeCountGmOrKg(
                        position, productPriceDto,
                        isAdd = false,
                        isGm = true, 0
                    )
                }

                lnKgIncrease.setOnClickListener {
                    itemListener.changeCountGmOrKg(
                        position,
                        productPriceDto,
                        isAdd = true,
                        isGm = false, 0
                    )
                }

                lnKgDecrease.setOnClickListener {
                    itemListener.changeCountGmOrKg(
                        position,
                        productPriceDto,
                        isAdd = false,
                        isGm = false, 0
                    )
                }

            }
        }

    }

    private fun dataExists(jsonArray: JSONArray, dataToFind: String): Boolean {
        return jsonArray.toString().contains("\"$dataToFind\"")
    }
}