package com.illagu.attil.cart.listener

import com.illagu.attil.cart.dto.CartListDto
import com.illagu.attil.home.offers.dto.ProductDto

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