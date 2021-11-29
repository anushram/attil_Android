package com.develop.sns.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.cart.dto.CartDetailsDto
import com.develop.sns.cart.dto.CartItemDto
import com.develop.sns.cart.listener.CartSubListener
import com.develop.sns.databinding.CartSubItemListTmplBinding
import com.develop.sns.utils.PreferenceHelper


class CartSubItemListAdapter(
    val context: Context,
    val cartItemDto: CartItemDto,
    val cartSubListener: CartSubListener,
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

    override fun getItemCount(): Int = cartItemDto.cartDetails.size

    override fun onBindViewHolder(holder: CartSubItemListAdapter.ViewHolder, position: Int) =
        holder.bind(cartItemDto.cartDetails[position], position)

    inner class ViewHolder(val binding: CartSubItemListTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartDetailsDto: CartDetailsDto, position: Int) {
            with(binding) {

                if (cartItemDto.packageType == "loose" && cartItemDto.offerType == "normal") {
                    val qty =
                        cartDetailsDto.cartSelectedMinUnit + (cartDetailsDto.cartSelectedMaxUnit * 1000)

                    lnQuantity.visibility = View.GONE
                    lnAdd.visibility = View.GONE
                    lnLooseAdd.visibility = View.VISIBLE

                    tvGmCount.text = cartDetailsDto.cartSelectedMinUnit.toString()
                    tvKgCount.text = cartDetailsDto.cartSelectedMaxUnit.toString()

                    tvOfferPercentage.text =
                        cartDetailsDto.offerPercentage.toString().plus(" ").plus("% OFF")

                } else if ((cartItemDto.packageType == "packed" && cartItemDto.offerType == "normal")
                    || (cartItemDto.packageType == "packed" && cartItemDto.offerType == "BOGO")
                    || (cartItemDto.packageType == "packed" && cartItemDto.offerType == "BOGE")
                ) {
                    lnQuantity.visibility = View.VISIBLE

                    tvQuantity.text =
                        cartDetailsDto.unit.toString().plus(" ").plus(cartDetailsDto.measureType)

                    lnAdd.visibility = View.VISIBLE
                    lnLooseAdd.visibility = View.GONE

                    tvCount.text = cartDetailsDto.cartSelectedItemCount.toString()

                    tvOfferPercentage.text =
                        cartDetailsDto.offerPercentage.toString().plus(" ").plus("% OFF")

                }


                lnIncrease.setOnClickListener {
                    cartSubListener.changeSubCount(position, cartDetailsDto, true,cartItemDto)
                }

                lnDecrease.setOnClickListener {
                    cartSubListener.changeSubCount(position, cartDetailsDto, false,cartItemDto)
                }

                lnGmIncrease.setOnClickListener {
                    cartSubListener.changeSubCountGmOrKg(
                        position, cartDetailsDto,
                        isAdd = true,
                        isGm = true,
                        ++clickGmPlusCount,cartItemDto
                    )
                }

                lnGmDecrease.setOnClickListener {
                    cartSubListener.changeSubCountGmOrKg(
                        position, cartDetailsDto,
                        isAdd = false,
                        isGm = true, 0,cartItemDto
                    )
                }

                lnKgIncrease.setOnClickListener {
                    cartSubListener.changeSubCountGmOrKg(
                        position,
                        cartDetailsDto,
                        isAdd = true,
                        isGm = false, 0,cartItemDto
                    )
                }

                lnKgDecrease.setOnClickListener {
                    cartSubListener.changeSubCountGmOrKg(
                        position,
                        cartDetailsDto,
                        isAdd = false,
                        isGm = false, 0,cartItemDto
                    )
                }

            }
        }

    }

}