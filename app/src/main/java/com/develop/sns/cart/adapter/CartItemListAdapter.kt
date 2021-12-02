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
import com.develop.sns.cart.dto.CartListDto
import com.develop.sns.cart.listener.CartListener
import com.develop.sns.cart.listener.CartSubListener
import com.develop.sns.databinding.CartListItemTmplBinding
import com.develop.sns.home.offers.dto.ProductDto
import com.develop.sns.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import java.io.Serializable


class CartItemListAdapter(
    val context: Context,
    val items: ArrayList<ProductDto>,
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

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items.get(position), position)

    inner class ViewHolder(val binding: CartListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(productDto: ProductDto, position: Int) {
            with(binding) {

                tvProductName.text = productDto.productName

                for (j in 0 until productDto.brandImage.size) {
                    Picasso.with(context).load(productDto.brandImage[j])
                        .placeholder(R.drawable.product)
                        .error(R.drawable.product)
                        .into(ivProduct)
                }

                var mrp = 0F
                var offerMrp = 0F
                var diff: Float

                val cartDetailsList = productDto.cartList

                for (i in 0 until cartDetailsList.size) {

                    val cartDetailsDto = cartDetailsList[i]

                    tvDate.text = cartDetailsDto.updatedAt

                    if (productDto.packageType == "loose" && productDto.offerType == "normal") {
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

                    } else if ((productDto.packageType == "packed" && productDto.offerType == "normal")
                        || (productDto.packageType == "packed" && productDto.offerType == "BOGO")
                        || (productDto.packageType == "packed" && productDto.offerType == "BOGE")
                    ) {
                        lnPack.visibility = View.VISIBLE
                        tvAvailable.visibility = View.GONE

                        if (productDto.cartList.size > 1) {
                            tvPack.text =
                                productDto.cartList.size.toString().plus(" ")
                                    .plus(context.getString(R.string.packs))
                        } else {
                            tvPack.text =
                                productDto.cartList.size.toString().plus(" ")
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

                    val cartList = productDto.cartList;
                    if (!cartList.isEmpty()) {
                        lvItems.visibility = View.VISIBLE
                        cartSubItemListAdapter =
                            CartSubItemListAdapter(
                                context,
                                cartList,
                                this@CartItemListAdapter,
                                position
                            )
                        lvItems.adapter = cartSubItemListAdapter
                        linearLayoutManager = LinearLayoutManager(context)
                        lvItems.layoutManager = linearLayoutManager
                    }

                }

                lnMain.setOnClickListener {
                    cartListener.selectItem(productDto)
                }

                ivCartDelete.setOnClickListener {
                    cartListener.remove(position)
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
            val cartListDto: CartListDto = items[itemGroupPosition].cartList[position]
            var quantity = cartListDto.cartSelectedItemCount
            quantity = if (isAdd) {
                val value: Int = quantity + 1
                value
            } else {
                val value: Int = quantity - 1
                value
            }
            if (quantity > 0) {
                if (quantity.toFloat() <= cartListDto.availability.toFloat()) {
                    cartListDto.cartSelectedItemCount = quantity
                    cartListener.handleItem(cartListDto, true, itemGroupPosition, position)
                } else {
                    cartListDto.cartSelectedItemCount = cartListDto.availability
                    cartListener.handleItem(cartListDto, false, itemGroupPosition, position)
                }
                cartSubItemListAdapter.notifyItemChanged(position, cartListDto)
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
            val cartListDto: CartListDto = items[itemGroupPosition].cartList[position]
            val quantity: Int

            val minUnit = cartListDto.cartSelectedMinUnit
            val maxUnit = cartListDto.cartSelectedMaxUnit

            if (isGm) {
                var minQuantity = Integer.parseInt(minUnit.toString())
                minQuantity = if (isAdd) {
                    if (count == 1) {
                        val value: Int = cartListDto.minUnit
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
                if (quantity.toFloat() < cartListDto.maxUnit * 1000.toFloat()) {
                    if (quantity.toFloat() < cartListDto.minUnit.toFloat()) {
                        Log.e("Less Than", "Min")
                        Log.e("Less Than", "Comes Here")
                        cartListDto.cartSelectedMinUnit = cartListDto.minUnit
                        cartListDto.cartSelectedMaxUnit = 0
                        cartListener.handleItem(
                            cartListDto,
                            false,
                            itemGroupPosition,
                            position
                        )
                        CartSubItemListAdapter.clickGmPlusCount = 0
                    } else {
                        maxQuantity = Integer.parseInt(maxUnit.toString())
                        val qty2 = minQuantity + (maxQuantity * 1000)
                        cartListDto.cartSelectedMinUnit = minQuantity
                        cartListDto.cartSelectedMaxUnit = maxQuantity
                        cartListener.handleItem(
                            cartListDto,
                            false,
                            itemGroupPosition,
                            position
                        )
                    }
                } else {
                    maxQuantity = Integer.parseInt(maxUnit.toString())
                    val qty3 = (maxQuantity * 1000)
                    cartListDto.cartSelectedMaxUnit = qty3
                    cartListener.handleItem(cartListDto, true, itemGroupPosition, position)
                }
                cartSubItemListAdapter.notifyItemChanged(position, cartListDto)
            } else {
                var maxQuantity = Integer.parseInt(maxUnit.toString())
                maxQuantity = if (isAdd) {
                    val value: Int = maxQuantity + 1
                    value
                } else {
                    val value: Int = maxQuantity - 1
                    value
                }
                if (maxQuantity.toFloat() < cartListDto.maxUnit.toFloat()) {
                    val minQuantity = Integer.parseInt(minUnit.toString())
                    minQuantity + (maxQuantity * 1000)
                    cartListDto.cartSelectedMinUnit = minQuantity
                    cartListDto.cartSelectedMaxUnit = maxQuantity
                    cartListener.handleItem(cartListDto, true, itemGroupPosition, position)
                } else {
                    cartListDto.cartSelectedMaxUnit = cartListDto.maxUnit
                    cartListener.handleItem(cartListDto, false, itemGroupPosition, position)
                }
                cartSubItemListAdapter.notifyItemChanged(position, cartListDto)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun removeItem(position: Int, itemGroupPosition: Int) {
        try {
            val cartDetailsDto = items[itemGroupPosition].cartList[position]
            cartListener.removeCartItem(itemGroupPosition, cartDetailsDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}