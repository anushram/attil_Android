package com.illagu.attil.cart.listener

interface CartSubListener {

    fun changeSubCount(
        position: Int,
        isAdd: Boolean,
        itemGroupPosition: Int
    )

    fun changeSubCountGmOrKg(
        position: Int,
        isAdd: Boolean,
        isGm: Boolean,
        count: Int,
        itemGroupPosition: Int
    )

    fun removeItem(position: Int, itemGroupPosition: Int)
}