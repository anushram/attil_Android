package com.develop.sns.cart.dto

import java.io.Serializable

class CartDetailsDto : Serializable {

    var cartItemId: String = ""
    var cartSelectedMinUnit: Int = 0
    var cartMinUnitMeasureType: String = ""
    var cartSelectedMaxUnit: Int = 0
    var cartMaxUnitMeasureType: String = ""
    var cartSelectedItemCount: Int = 0

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