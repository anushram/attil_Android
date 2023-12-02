package com.illagu.attil.home.offers.listener

import com.illagu.attil.home.offers.dto.ProductPriceDto

interface ItemListener {
    fun changeCount(position: Int, itemDto: ProductPriceDto, isAdd: Boolean)

    fun changeCountGmOrKg(
        position: Int,
        productPriceDto: ProductPriceDto,
        isAdd: Boolean,
        isGm: Boolean,
        count: Int,
    )

    fun selectItem(position: Int, productPriceDto: ProductPriceDto, isSelect: Boolean)

    fun addItemFromCart(position: Int, productPriceDto: ProductPriceDto)
}