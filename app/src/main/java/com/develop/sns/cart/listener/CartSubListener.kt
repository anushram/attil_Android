package com.develop.sns.cart.listener

import com.develop.sns.cart.dto.CartDetailsDto
import com.develop.sns.cart.dto.CartItemDto

interface CartSubListener {

    fun changeSubCount(
        position: Int,
        cartDetailsDto: CartDetailsDto,
        isAdd: Boolean,
        cartItemDto: CartItemDto
    )

    fun changeSubCountGmOrKg(
        position: Int,
        cartDetailsDto: CartDetailsDto,
        isAdd: Boolean,
        isGm: Boolean,
        count: Int,
        cartItemDto: CartItemDto
    )
}