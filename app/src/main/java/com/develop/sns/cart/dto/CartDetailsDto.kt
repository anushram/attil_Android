package com.develop.sns.cart.dto

import java.io.Serializable

class CartDetailsDto : Serializable {

    var packageType: String = ""
    var offerType: String = ""
    var cartItemId: String = ""
    var availability: Int = 0
    var cartSelectedMinUnit: Int = 0
    var cartMinUnitMeasureType: String = ""
    var cartSelectedMaxUnit: Int = 0
    var cartMaxUnitMeasureType: String = ""
    var minUnitMeasureType: String = ""
    var minUnit: Int = 0
    var maxUnit: Int = 0
    var maxUnitMeasureType: String = ""
    var cartSelectedItemCount: Int = 0
    var unit: Int = 0
    var normalPrice: Int = 0
    var attilPrice: Int = 0
    var updatedAt: String = ""
    var measureType: String = ""
    var offerMeasureType: String = ""
    var offerUnit: Int = 0
    var offerPercentage: Int = 0
    var bogeProductName: String = ""
    var bogeProductImg: String = ""
    var bogeUnit: Int = 0
    var bogeMeasureType: String = ""

    override fun toString(): String {
        return cartItemId
    }

    override fun equals(other: Any?): Boolean {
        if (other is CartDetailsDto) {
            return cartItemId == other.cartItemId
        }
        return false
    }

    override fun hashCode(): Int {
        return cartItemId.hashCode()
    }

}
