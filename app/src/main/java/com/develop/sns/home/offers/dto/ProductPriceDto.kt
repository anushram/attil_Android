package com.develop.sns.home.offers.dto

import java.io.Serializable

class ProductPriceDto : Serializable {
    var id: String = ""
    var measureType: String = ""
    var normalPrice: Int = 0
    var attilPrice: Int = 0
    var unit: Int = 0
    var availability: Int = 0
    var minUnitMeasureType: String = ""
    var minUnit: Int = 0
    var maxUnitMeasureType: String = ""
    var maxUnit: Int = 0
    var offerMeasureType: String = ""
    var offerUnit: Int = 0
    var offerPercentage: Int = 0
    var bogeProductName: String = ""
    var bogeProductImg: String = ""
    var bogeUnit: Int = 0
    var bogeMeasureType: String = ""
    var quantity: Int = 0
    var selectedFlag: Boolean = false
    var packageType: String = ""
    var offerType: String = ""


    override fun toString(): String {
        return measureType
    }

    override fun equals(other: Any?): Boolean {
        if (other is ProductPriceDto) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}