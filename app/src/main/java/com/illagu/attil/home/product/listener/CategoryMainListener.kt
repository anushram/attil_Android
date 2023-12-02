package com.illagu.attil.home.product.listener

import com.talentmicro.icanrefer.dto.CategoryMainDto

interface CategoryMainListener {
    fun selectCategoryItem(categoryMainDto: CategoryMainDto, position: Int)
}