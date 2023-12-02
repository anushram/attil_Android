package com.illagu.attil.home.offers.listener

import com.illagu.attil.home.offers.dto.ProductDto

interface TopOfferListener {
    fun selectTopOfferItem(itemDto: ProductDto)
}