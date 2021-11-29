package com.develop.sns.cart.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.cart.dto.CartDetailsDto
import com.develop.sns.cart.dto.CartItemDto
import com.develop.sns.cart.listener.CartListener
import com.develop.sns.cart.listener.CartSubListener
import com.develop.sns.customviews.CustomRecyclerView
import com.develop.sns.databinding.CartListItemTmplBinding
import com.develop.sns.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import java.lang.Exception


class CartItemListAdapter(
    val context: Context,
    val items: ArrayList<CartItemDto>?,
    val cartListener: CartListener,
) : RecyclerView.Adapter<CartItemListAdapter.ViewHolder>(), CartSubListener {

    var preferenceHelper = PreferenceHelper(context)
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var cartSubItemListAdapter: CartSubItemListAdapter

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
                        .into(ivProduct)
                }

                var mrp = 0F
                var offerMrp = 0F
                var diff: Float

                val cartDetailsList = cartItemDto.cartDetails

                for (i in 0 until cartDetailsList.size) {

                    val cartDetailsDto = cartDetailsList[i]

                    tvDate.text = cartDetailsDto.updatedAt

                    if (cartItemDto.packageType == "loose" && cartItemDto.offerType == "normal") {
                        lnPack.visibility = View.GONE
                        tvAvailable.visibility = View.VISIBLE

                        val availableText = context.getString(R.string.available).plus(" ")
                            .plus(context.getString(R.string.upto))
                            .plus(cartDetailsDto.maxUnit).plus(" ")
                            .plus(cartDetailsDto.maxUnitMeasureType)

                        tvAvailable.text = availableText

                        val qty =
                            cartDetailsDto.cartSelectedMinUnit + (cartDetailsDto.cartSelectedMaxUnit * 1000)

                        val result = qty.toFloat() / cartDetailsDto.unit.toFloat()
                        mrp += result.times(cartDetailsDto.normalPrice)
                        offerMrp += result.times(cartDetailsDto.attilPrice)
                        diff = (mrp - offerMrp)

                        tvMrp.text =
                            context.getString(R.string.totally).plus(" ")
                                .plus(context.getString(R.string.Rs)).plus("")
                                .plus("%.2f".format(mrp))
                        tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                        tvOfferPrice.text =
                            context.getString(R.string.Rs).plus("")
                                .plus("%.2f".format(offerMrp))

                        tvSave.text =
                            context.getString(R.string.totally).plus(" ")
                                .plus(context.getString(R.string.you_save)).plus(" ")
                                .plus(context.getString(R.string.Rs)).plus("")
                                .plus("%.2f".format(diff))

                    } else if ((cartItemDto.packageType == "packed" && cartItemDto.offerType == "normal")
                        || (cartItemDto.packageType == "packed" && cartItemDto.offerType == "BOGO")
                        || (cartItemDto.packageType == "packed" && cartItemDto.offerType == "BOGE")
                    ) {
                        lnPack.visibility = View.VISIBLE
                        tvAvailable.visibility = View.GONE

                        if (cartItemDto.cartDetails.size > 1) {
                            tvPack.text =
                                cartItemDto.cartDetails.size.toString().plus(" ")
                                    .plus(context.getString(R.string.packs))
                        } else {
                            tvPack.text =
                                cartItemDto.cartDetails.size.toString().plus(" ")
                                    .plus(context.getString(R.string.pack))
                        }
                        val qty = cartDetailsDto.cartSelectedItemCount

                        mrp += qty.times(cartDetailsDto.normalPrice)
                        offerMrp += qty.times(cartDetailsDto.attilPrice)
                        diff = (mrp - offerMrp)

                        tvMrp.text =
                            context.getString(R.string.totally).plus(" ")
                                .plus(context.getString(R.string.Rs)).plus("")
                                .plus("%.2f".format(mrp))
                        tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                        tvOfferPrice.text =
                            context.getString(R.string.Rs).plus("")
                                .plus("%.2f".format(offerMrp))

                        tvSave.text =
                            context.getString(R.string.totally).plus(" ")
                                .plus(context.getString(R.string.you_save)).plus(" ")
                                .plus(context.getString(R.string.Rs)).plus("")
                                .plus("%.2f".format(diff))

                    }

                    lvItems.visibility = View.VISIBLE
                    linearLayoutManager = LinearLayoutManager(context)
                    lvItems.layoutManager = linearLayoutManager
                    cartSubItemListAdapter =
                        CartSubItemListAdapter(context, cartItemDto, this@CartItemListAdapter)
                    lvItems.adapter = cartSubItemListAdapter

                }

                with(cartSubItemListAdapter) {

                    this.notifyDataSetChanged()
                }

                lnMain.setOnClickListener {
                    val itemDto: CartItemDto = items!!.get(position)
                    cartListener.selectItem(itemDto)
                }

            }
        }

    }

    private fun populateItemList(cartItemDto: CartItemDto, lvItems: CustomRecyclerView) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun changeSubCount(
        position: Int,
        cartDetailsDto: CartDetailsDto,
        isAdd: Boolean,
        cartItemDto: CartItemDto
    ) {
        try {
            cartListener.changeCount(position, cartDetailsDto, isAdd, cartItemDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun changeSubCountGmOrKg(
        position: Int,
        cartDetailsDto: CartDetailsDto,
        isAdd: Boolean,
        isGm: Boolean,
        count: Int,
        cartItemDto: CartItemDto
    ) {
        try {
            cartListener.changeCountGmOrKg(
                position,
                cartDetailsDto,
                isAdd,
                isGm,
                count,
                cartItemDto
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}