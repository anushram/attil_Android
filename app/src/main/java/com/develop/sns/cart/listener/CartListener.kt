package com.develop.sns.cart.listener

import com.develop.sns.cart.dto.CartDetailsDto
import com.develop.sns.cart.dto.CartItemDto
import com.develop.sns.home.offers.dto.NormalOfferDto
import com.develop.sns.home.offers.dto.NormalOfferPriceDto

interface CartListener {

    fun selectItem(cartItemDto: CartItemDto)

    fun handleItem(
        cartDetailsDto: CartDetailsDto,
        isAdd: Boolean,
        itemGroupPosition: Int,
        position: Int
    )

    fun remove(
        itemGroupPosition: Int,
        position: Int
    )

}