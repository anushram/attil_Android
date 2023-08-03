package com.illagu.attil.home.product.listener

import com.talentmicro.icanrefer.dto.CategoryProductDto

interface CategoryProductListener {
    fun selectCategoryProductItem(categoryMainDto: CategoryProductDto, position: Int)
}