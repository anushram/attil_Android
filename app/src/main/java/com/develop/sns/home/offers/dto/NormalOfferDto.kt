package com.develop.sns.home.offers.dto

import java.io.Serializable

class NormalOfferDto : Serializable {
    var id: String? = null
    var productCode: String? = null
    var productName: String? = null
    var brandImage: ArrayList<String>? = null
    var brandId: String? = null
    var brandName: String? = null
    var packageType: String? = null
    var offerType: String? = null
    var description: String? = null
    var createdAtTZ: String? = null
    var priceDetails: ArrayList<NormalOfferPriceDto>? = null


    override fun toString(): String {
        return productName!!
    }

    override fun equals(o: Any?): Boolean {
        if (o is NormalOfferDto) {
            return id == o.id
        }
        return false
    }

    override fun hashCode(): Int {
        return productName?.hashCode() ?: 0
    }
}