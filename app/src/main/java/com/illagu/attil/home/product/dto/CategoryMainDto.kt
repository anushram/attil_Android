package com.talentmicro.icanrefer.dto

import java.io.Serializable

/**
 * Created by Sujit on 31-08-2017.
 */
class CategoryMainDto : Serializable {
    var id = ""
    var categoryName = ""
    var categoryImage: String = ""
    var isSelected = false

    override fun toString(): String {
        return categoryName
    }

}