package com.develop.sns.home.product.listener

import com.talentmicro.icanrefer.dto.CategoryMainDto

interface CategoryMainListener {
    fun selectCategoryItem(categoryMainDto: CategoryMainDto, position: Int)
}