package com.develop.sns.cart.adapter

import android.content.Context
import android.graphics.Paint
import android.util.Log
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
import com.develop.sns.databinding.CartListItemTmplBinding
import com.develop.sns.home.details.adapter.ItemDetailsListAdapter
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

                    val cartList = cartItemDto.cartDetails;
                    if (!cartList.isEmpty()) {
                        lvItems.visibility = View.VISIBLE
                        val cartSubItemListAdapter =
                            CartSubItemListAdapter(
                                context,
                                cartList,
                                this@CartItemListAdapter,
                                position
                            )
                        lvItems.adapter = cartSubItemListAdapter
                        linearLayoutManager = LinearLayoutManager(context)
                        lvItems.layoutManager = linearLayoutManager

                        cartItemDto.cartSubItemListAdapter = cartSubItemListAdapter
                    }

                }

                lnMain.setOnClickListener {
                    val itemDto: CartItemDto = items!!.get(position)
                    cartListener.selectItem(itemDto)
                }

            }
        }

    }

    override fun changeSubCount(
        position: Int,
        isAdd: Boolean,
        itemGroupPosition: Int
    ) {
        try {
            val cartSubItemListAdapter = items!![itemGroupPosition].cartSubItemListAdapter
            if (cartSubItemListAdapter != null) {
                val cartDetailsDto: CartDetailsDto = items[itemGroupPosition].cartDetails[position]
                var quantity = cartDetailsDto.cartSelectedItemCount
                quantity = if (isAdd) {
                    val value: Int = quantity + 1
                    value
                } else {
                    val value: Int = quantity - 1
                    value
                }
                if (quantity > 0) {
                    if (quantity.toFloat() <= cartDetailsDto.availability.toFloat()) {
                        cartDetailsDto.cartSelectedItemCount = quantity
                        cartListener.handleItem(cartDetailsDto, true, itemGroupPosition, position)
                    } else {
                        cartDetailsDto.cartSelectedItemCount = cartDetailsDto.availability
                        cartListener.handleItem(cartDetailsDto, false, itemGroupPosition, position)
                    }
                    cartSubItemListAdapter.notifyItemChanged(position, cartDetailsDto)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun changeSubCountGmOrKg(
        position: Int,
        isAdd: Boolean,
        isGm: Boolean,
        count: Int,
        itemGroupPosition: Int
    ) {
        try {
            val cartSubItemListAdapter = items!![itemGroupPosition].cartSubItemListAdapter
            if (cartSubItemListAdapter != null) {
                val cartDetailsDto: CartDetailsDto = items[itemGroupPosition].cartDetails[position]
                val quantity: Int

                val minUnit = cartDetailsDto.cartSelectedMinUnit
                val maxUnit = cartDetailsDto.cartSelectedMaxUnit

                if (isGm) {
                    var minQuantity = Integer.parseInt(minUnit.toString())
                    minQuantity = if (isAdd) {
                        if (count == 1) {
                            val value: Int = cartDetailsDto.minUnit
                            value
                        } else {
                            val value: Int = minQuantity + 50
                            value
                        }
                    } else {
                        val value: Int = minQuantity - 50
                        value
                    }

                    var maxQuantity = Integer.parseInt(maxUnit.toString())
                    quantity = minQuantity + (maxQuantity * 1000)
                    if (quantity.toFloat() < cartDetailsDto.maxUnit * 1000.toFloat()) {
                        if (quantity.toFloat() < cartDetailsDto.minUnit.toFloat()) {
                            Log.e("Less Than", "Min")
                            Log.e("Less Than", "Comes Here")
                            cartDetailsDto.cartSelectedMinUnit = 0
                            cartDetailsDto.cartSelectedMaxUnit = 0
                            cartListener.handleItem(
                                cartDetailsDto,
                                false,
                                itemGroupPosition,
                                position
                            )
                            CartSubItemListAdapter.clickGmPlusCount = 0
                        } else {
                            maxQuantity = Integer.parseInt(maxUnit.toString())
                            val qty2 = minQuantity + (maxQuantity * 1000)
                            cartDetailsDto.cartSelectedMinUnit = minQuantity
                            cartDetailsDto.cartSelectedMaxUnit = maxQuantity
                            cartListener.handleItem(
                                cartDetailsDto,
                                false,
                                itemGroupPosition,
                                position
                            )
                        }
                    } else {
                        maxQuantity = Integer.parseInt(maxUnit.toString())
                        val qty3 = (maxQuantity * 1000)
                        cartDetailsDto.cartSelectedMaxUnit = qty3
                        cartListener.handleItem(cartDetailsDto, true, itemGroupPosition, position)
                    }
                    cartSubItemListAdapter.notifyItemChanged(position, cartDetailsDto)
                } else {
                    var maxQuantity = Integer.parseInt(maxUnit.toString())
                    maxQuantity = if (isAdd) {
                        val value: Int = maxQuantity + 1
                        value
                    } else {
                        val value: Int = maxQuantity - 1
                        value
                    }
                    if (maxQuantity.toFloat() < cartDetailsDto.maxUnit.toFloat()) {
                        val minQuantity = Integer.parseInt(minUnit.toString())
                        val qty4 = minQuantity + (maxQuantity * 1000)
                        cartDetailsDto.cartSelectedMinUnit = minQuantity
                        cartDetailsDto.cartSelectedMaxUnit = maxQuantity
                        cartListener.handleItem(cartDetailsDto, true, itemGroupPosition, position)
                    } else {
                        cartDetailsDto.cartSelectedMaxUnit = cartDetailsDto.maxUnit
                        cartListener.handleItem(cartDetailsDto, false, itemGroupPosition, position)
                    }
                    cartSubItemListAdapter.notifyItemChanged(position, cartDetailsDto)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}