package com.develop.sns.cart.listener

import com.develop.sns.cart.dto.CartDetailsDto
import com.develop.sns.cart.dto.CartItemDto
import com.develop.sns.home.offers.dto.NormalOfferDto
import com.develop.sns.home.offers.dto.NormalOfferPriceDto

interface CartListener {

    fun selectItem(cartItemDto: CartItemDto)

    fun changeCount(
        position: Int,
        cartDetailsDto: CartDetailsDto,
        isAdd: Boolean,
        cartItemDto: CartItemDto
    )

    fun changeCountGmOrKg(
        position: Int,
        cartDetailsDto: CartDetailsDto,
        isAdd: Boolean,
        isGm: Boolean,
        count: Int,
        cartItemDto: CartItemDto
    )

}