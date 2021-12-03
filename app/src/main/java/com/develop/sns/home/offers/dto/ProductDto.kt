package com.develop.sns.home.offers.dto

import com.develop.sns.cart.adapter.CartSubItemListAdapter
import com.develop.sns.cart.dto.CartListDto
import java.io.Serializable

class ProductDto : Serializable {

    var id: String = ""
    var productId: String = ""
    var productCode: String = ""
    var productName: String = ""
    var brandImage: ArrayList<String> = ArrayList()
    var sliderImage: ArrayList<String> = ArrayList()
    var brandId: String = ""
    var brandName: String = ""
    var packageType: String = ""
    var offerType: String = ""
    var description: String = ""
    var createdAtTZ: String = ""
    var priceDetails: ArrayList<ProductPriceDto> = ArrayList()
    var cartList: ArrayList<CartListDto>? = null

    override fun toString(): String {
        return productName
    }

    override fun equals(other: Any?): Boolean {
        if (other is ProductDto) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return productName.hashCode() ?: 0
    }
}