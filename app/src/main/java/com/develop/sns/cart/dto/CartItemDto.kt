package com.develop.sns.cart.dto

import com.develop.sns.home.offers.dto.NormalOfferPriceDto
import java.io.Serializable

class CartItemDto : Serializable {

    var id: String = ""
    var productId: String = ""
    var productCode: String = ""
    var productName: String = ""
    var brandImage: ArrayList<String> = ArrayList()
    var brandId: String = ""
    var brandName: String = ""
    var packageType: String = ""
    var offerType: String = ""
    var description: String = ""
    var createdAtTZ: String = ""
    var priceDetails: ArrayList<NormalOfferPriceDto> = ArrayList()
    var cartDetails: ArrayList<CartDetailsDto> = ArrayList()

    override fun toString(): String {
        return productName
    }

    override fun equals(other: Any?): Boolean {
        if (other is CartItemDto) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}