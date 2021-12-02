package com.develop.sns.cart.listener

import com.develop.sns.cart.dto.CartListDto
import com.develop.sns.home.offers.dto.ProductDto

interface CartListener {

    fun selectItem(productDto: ProductDto)

    fun handleItem(
        cartListDto: CartListDto,
        isAdd: Boolean,
        itemGroupPosition: Int,
        position: Int
    )

    fun remove(
        position: Int
    )

    fun removeCartItem(
        itemGroupPosition: Int,
        cartListDto: CartListDto
    )

}