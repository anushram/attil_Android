package com.develop.sns.cart.listener

import com.develop.sns.cart.dto.CartDetailsDto
import com.develop.sns.cart.dto.CartItemDto

interface CartSubListener {

    fun changeSubCount(
        position: Int,
        isAdd: Boolean,
        itemGroupPosition: Int
    )

    fun changeSubCountGmOrKg(
        position: Int,
        isAdd: Boolean,
        isGm: Boolean,
        count: Int,
        itemGroupPosition: Int
    )
}