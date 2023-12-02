package com.talentmicro.icanrefer.dto

import java.io.Serializable

/**
 * Created by Sujit on 31-08-2017.
 */
class CategoryProductDto : Serializable {
    var id = ""
    var commonName = ""
    var productName: String = ""
    var productCode: String = ""
    var commonImage: String = ""
    var commonProductId: String = ""
    var varieties: Int = 0
    var brands: Int = 0
    var isSelected = false

    override fun toString(): String {
        return commonName
    }

}