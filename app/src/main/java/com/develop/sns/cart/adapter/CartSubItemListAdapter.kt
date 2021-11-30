package com.develop.sns.cart.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.cart.dto.CartDetailsDto
import com.develop.sns.cart.listener.CartSubListener
import com.develop.sns.databinding.CartSubItemListTmplBinding
import com.develop.sns.utils.PreferenceHelper


class CartSubItemListAdapter(
    val context: Context,
    val cartList: ArrayList<CartDetailsDto>,
    val cartSubListener: CartSubListener,
    var itemGroupPosition: Int,
) : RecyclerView.Adapter<CartSubItemListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)

    companion object {
        var clickGmPlusCount = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartSubItemListTmplBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = cartList.size

    override fun onBindViewHolder(holder: CartSubItemListAdapter.ViewHolder, position: Int) =
        holder.bind(cartList[position], position)

    inner class ViewHolder(val binding: CartSubItemListTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartDetailsDto: CartDetailsDto, position: Int) {
            with(binding) {

                if (cartDetailsDto.packageType == "loose" && cartDetailsDto.offerType == "normal") {
                    val qty =
                        cartDetailsDto.cartSelectedMinUnit + (cartDetailsDto.cartSelectedMaxUnit * 1000)

                    lnQuantity.visibility = View.GONE
                    lnAdd.visibility = View.GONE
                    lnLooseAdd.visibility = View.VISIBLE

                    val kg = qty.toDouble() * 0.001
                    var minUnit = "0"
                    var maxUnit = "0"
                    val qtyStr = "%.3f".format(kg)
                    minUnit = qtyStr.split(".")[1]
                    maxUnit = qtyStr.split(".")[0]
                    Log.e("min", minUnit)
                    Log.e("max", maxUnit)
                    if (minUnit == "000") {
                        minUnit = "0"
                    }

                    tvGmCount.text = minUnit
                    tvKgCount.text = maxUnit

                    tvOfferPercentage.text =
                        cartDetailsDto.offerPercentage.toString().plus(" ").plus("% OFF")

                } else if ((cartDetailsDto.packageType == "packed" && cartDetailsDto.offerType == "normal")
                    || (cartDetailsDto.packageType == "packed" && cartDetailsDto.offerType == "BOGO")
                    || (cartDetailsDto.packageType == "packed" && cartDetailsDto.offerType == "BOGE")
                ) {
                    lnQuantity.visibility = View.VISIBLE

                    tvQuantity.text =
                        cartDetailsDto.unit.toString().plus(" ").plus(cartDetailsDto.measureType)

                    lnAdd.visibility = View.VISIBLE
                    lnLooseAdd.visibility = View.GONE
                    if (cartDetailsDto.cartSelectedItemCount > 0) {
                        tvCount.text = cartDetailsDto.cartSelectedItemCount.toString()
                    }
                    tvOfferPercentage.text =
                        cartDetailsDto.offerPercentage.toString().plus(" ").plus("% OFF")

                }


                lnIncrease.setOnClickListener {
                    cartSubListener.changeSubCount(
                        position,
                        true,
                        itemGroupPosition
                    )
                }

                lnDecrease.setOnClickListener {
                    cartSubListener.changeSubCount(
                        position,
                        false,
                        itemGroupPosition
                    )
                }

                lnGmIncrease.setOnClickListener {
                    cartSubListener.changeSubCountGmOrKg(
                        position,
                        isAdd = true,
                        isGm = true,
                        ++clickGmPlusCount,
                        itemGroupPosition
                    )
                }

                lnGmDecrease.setOnClickListener {
                    cartSubListener.changeSubCountGmOrKg(
                        position,
                        isAdd = false,
                        isGm = true,
                        0,
                        itemGroupPosition
                    )
                }

                lnKgIncrease.setOnClickListener {
                    cartSubListener.changeSubCountGmOrKg(
                        position,
                        isAdd = true,
                        isGm = false,
                        0,
                        itemGroupPosition
                    )
                }

                lnKgDecrease.setOnClickListener {
                    cartSubListener.changeSubCountGmOrKg(
                        position,
                        isAdd = false,
                        isGm = false,
                        0,
                        itemGroupPosition
                    )
                }

            }
        }

    }

}