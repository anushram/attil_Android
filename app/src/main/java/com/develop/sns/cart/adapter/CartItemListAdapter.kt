package com.develop.sns.cart.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.cart.dto.CartItemDto
import com.develop.sns.cart.listener.CartListener
import com.develop.sns.databinding.CartListItemTmplBinding
import com.develop.sns.home.offers.dto.NormalOfferPriceDto
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import org.json.JSONArray


class CartItemListAdapter(
    val context: Context,
    val items: ArrayList<CartItemDto>?,
    val cartListener: CartListener,
) : RecyclerView.Adapter<CartItemListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""
    var mrp = 0.0
    var offerMrp = 0.0
    var minUnit = 0.0
    var diff = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartListItemTmplBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items?.get(position)!!, position)

    inner class ViewHolder(val binding: CartListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItemDto: CartItemDto, position: Int) {
            with(binding) {
                tvProductName.text = cartItemDto.productName
                for (j in 0 until cartItemDto.brandImage.size) {
                    Picasso.with(context).load(cartItemDto.brandImage[j])
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(ivProduct);
                }

                val obj = preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_MIN_UNITS);
                val jsonArray = JSONArray(obj)

                val normalOfferPriceDto: NormalOfferPriceDto = cartItemDto.priceDetails[0]
                if (cartItemDto.packageType == "loose" && cartItemDto.offerType == "normal") {

                    measureText = context.getString(R.string.available).plus(" ")
                        .plus(context.getString(R.string.upto)).plus(" ")
                        .plus(normalOfferPriceDto.maxUnit).plus(" ")
                        .plus(normalOfferPriceDto.maxUnitMeasureType)

                    minUnit = normalOfferPriceDto.minUnit!!.toDouble()
                    if (!userexists(jsonArray, normalOfferPriceDto.minUnitMeasureType!!)) {
                        minUnit = (normalOfferPriceDto.minUnit!! * 1000).toDouble()
                    }

                    val result: Double = minUnit.div(normalOfferPriceDto.unit!!.toDouble())
                    mrp = result.times(normalOfferPriceDto.normalPrice!!)
                    offerMrp = result.times(normalOfferPriceDto.attilPrice!!)
                    diff = (mrp - offerMrp)

                    tvOfferPercentage.visibility = View.VISIBLE
                    tvOfferPercentage.text =
                        normalOfferPriceDto.offerPercentage!!.toString().plus("% OFF")

                    tvAvailability.visibility = View.VISIBLE
                    tvAvailability.text = measureText

                    tvMrp.text =
                        context.getString(R.string.totally).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    tvSave.text =
                        context.getString(R.string.totally).plus(" ")
                            .plus(context.getString(R.string.you_save)).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))

                } else if (cartItemDto.packageType.equals("packed") && cartItemDto.offerType.equals("normal")) {

                    measureText = context.getString(R.string.starts).plus(" @")
                        .plus(normalOfferPriceDto.unit).plus(" ")
                        .plus(normalOfferPriceDto.measureType)

                    mrp = normalOfferPriceDto.normalPrice!!.toDouble()
                    offerMrp = normalOfferPriceDto.attilPrice!!.toDouble()
                    diff = (mrp - offerMrp)

                    tvOfferPercentage.visibility = View.VISIBLE
                    tvOfferPercentage.text =
                        normalOfferPriceDto.offerPercentage.toString().plus("-")
                            .plus(cartItemDto.priceDetails[cartItemDto.priceDetails.size - 1].offerPercentage)
                            .plus("% OFF")

                    tvAvailability.visibility = View.GONE

                    tvMrp.text =
                        context.getString(R.string.totally).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    tvSave.text =
                        context.getString(R.string.totally).plus(" ")
                            .plus(context.getString(R.string.you_save)).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))

                } else if (cartItemDto.packageType == "packed" && cartItemDto.offerType == "BOGO") {

                    mrp = normalOfferPriceDto.normalPrice!!.toDouble()
                    offerMrp = normalOfferPriceDto.attilPrice!!.toDouble()
                    diff = (mrp - offerMrp)

                    tvMrp.text =
                        context.getString(R.string.totally).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    tvSave.text =
                        context.getString(R.string.totally).plus(" ")
                            .plus(context.getString(R.string.you_save)).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))

                    tvOfferPercentage.visibility = View.GONE
                    tvAvailability.visibility = View.GONE

                } else if (cartItemDto.packageType.equals("packed") && cartItemDto.offerType.equals(
                        "BOGE",
                        false
                    )
                ) {
                    mrp = normalOfferPriceDto.normalPrice!!.toDouble()
                    offerMrp = normalOfferPriceDto.attilPrice!!.toDouble()
                    diff = (mrp - offerMrp)

                    tvMrp.text =
                        context.getString(R.string.totally).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("").plus("%.2f".format(mrp))
                    tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    tvOfferPrice.text =
                        context.getString(R.string.Rs).plus("").plus("%.2f".format(offerMrp))

                    tvSave.text =
                        context.getString(R.string.totally).plus(" ")
                            .plus(context.getString(R.string.you_save)).plus(" ")
                            .plus(context.getString(R.string.Rs)).plus("")
                            .plus("%.2f".format(diff))

                    tvOfferPercentage.visibility = View.GONE
                    tvAvailability.visibility = View.GONE
                }

                lnMain.setOnClickListener {
                    val itemDto: CartItemDto = items!!.get(position)
                    cartListener.selectItem(itemDto)
                }

            }
        }

    }

    private fun userexists(jsonArray: JSONArray, usernameToFind: String): Boolean {
        return jsonArray.toString().contains("\"$usernameToFind\"")
    }
}