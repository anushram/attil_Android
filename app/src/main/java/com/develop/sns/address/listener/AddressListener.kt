package com.develop.sns.address.listener

import com.develop.sns.address.dto.AddressListDto

interface AddressListener {

    fun selectItem(addressListDto: AddressListDto)


    fun edit(addressListDto: AddressListDto)


}