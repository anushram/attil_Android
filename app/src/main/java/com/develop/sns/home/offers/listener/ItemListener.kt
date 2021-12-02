package com.develop.sns.home.offers.listener

import com.develop.sns.home.offers.dto.ProductPriceDto

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

    fun takeActions(position: Int)

}