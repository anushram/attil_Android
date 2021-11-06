package com.develop.sns.cart.listener

import com.develop.sns.cart.dto.CartItemDto
import com.develop.sns.home.offers.dto.NormalOfferDto

interface CartListener {
    fun selectItem(itemDto: CartItemDto)
}