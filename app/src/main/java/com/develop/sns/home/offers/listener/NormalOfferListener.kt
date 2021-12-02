package com.develop.sns.home.offers.listener

import com.develop.sns.home.offers.dto.ProductDto

interface NormalOfferListener {
    fun selectNormalOfferItem(itemDto: ProductDto)
}