package com.develop.sns.home.offers.listener

import com.develop.sns.home.dto.NormalOfferDto

interface NormalOfferListener {
    fun selectNormalOfferItem(itemDto: NormalOfferDto)
}