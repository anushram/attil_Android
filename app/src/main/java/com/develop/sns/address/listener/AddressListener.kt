package com.develop.sns.address.listener

import com.develop.sns.address.dto.AddressListDto
import com.develop.sns.cart.dto.CartListDto
import com.develop.sns.home.offers.dto.ProductDto

interface AddressListener {

    fun selectItem(addressListDto: AddressListDto)


    fun edit(addressListDto: AddressListDto)


}