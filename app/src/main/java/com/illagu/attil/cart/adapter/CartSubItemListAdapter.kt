package com.illagu.attil.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.illagu.attil.cart.dto.CartListDto
import com.illagu.attil.cart.listener.CartSubListener
import com.illagu.attil.databinding.CartSubItemListTmplBinding
import com.illagu.attil.utils.PreferenceHelper


class CartSubItemListAdapter(
    val context: Context,
    private val cartList: ArrayList<CartListDto>,
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
        fun bind(cartListDto: CartListDto, position: Int) {
            with(binding) {

                if (cartListDto.packageType == "loose" && cartListDto.offerType == "normal") {
                    val qty =
                        cartListDto.cartSelectedMinUnit + (cartListDto.cartSelectedMaxUnit * 1000)

                    lnQuantity.visibility = View.GONE
                    lnAdd.visibility = View.GONE
                    lnLooseAdd.visibility = View.VISIBLE

                    val kg = qty.toDouble() * 0.001
                    var minUnit = "0"
                    var maxUnit = "0"
                    val qtyStr = "%.3f".format(kg)
                    minUnit = qtyStr.split(".")[1]
                    maxUnit = qtyStr.split(".")[0]
                    //Log.e("min", minUnit)
                    //Log.e("max", maxUnit)
                    if (minUnit == "000") {
                        minUnit = "0"
                    }

                    tvGmCount.text = minUnit
                    tvKgCount.text = maxUnit

                    tvOfferPercentage.text =
                        cartListDto.offerPercentage.toString().plus("% OFF")

                } else {
                    lnQuantity.visibility = View.VISIBLE

                    tvQuantity.text =
                        cartListDto.unit.toString().plus(" ").plus(cartListDto.measureType)

                    lnAdd.visibility = View.VISIBLE
                    lnLooseAdd.visibility = View.GONE
                    if (cartListDto.cartSelectedItemCount > 0) {
                        tvCount.text = cartListDto.cartSelectedItemCount.toString()
                    }
                    tvOfferPercentage.text =
                        cartListDto.offerPercentage.toString().plus("% OFF")

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

                lnClose.setOnClickListener {
                    cartSubListener.removeItem(position, itemGroupPosition)
                }

            }
        }

    }

}