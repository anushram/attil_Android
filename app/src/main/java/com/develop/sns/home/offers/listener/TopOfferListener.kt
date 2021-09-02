package com.develop.sns.home.offers.listener

import com.develop.sns.home.dto.NormalOfferDto

interface TopOfferListener {
    fun selectTopOfferItem(itemDto: NormalOfferDto)
}