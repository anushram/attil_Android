package com.develop.sns.home.product.listener

import com.talentmicro.icanrefer.dto.CategoryMainDto
import com.talentmicro.icanrefer.dto.CategoryProductDto

interface CategoryProductListener {
    fun selectCategoryProductItem(categoryMainDto: CategoryProductDto, position: Int)
}