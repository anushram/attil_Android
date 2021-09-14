package com.develop.sns.home.offers.listener

import com.develop.sns.home.offers.dto.NormalOfferPriceDto

interface ItemListener {
    fun changeCount(position: Int, itemDto: NormalOfferPriceDto?, isAdd: Boolean)
    fun selectItem(position: Int, itemDto: NormalOfferPriceDto?, isSelect: Boolean)
    fun takeActions(position: Int)
}