package com.develop.sns.home.offers.listener

import com.develop.sns.home.offers.dto.NormalOfferDto

interface TopOfferListener {
    fun selectTopOfferItem(itemDto: NormalOfferDto)
}