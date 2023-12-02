package com.illagu.attil.address.listener

import com.illagu.attil.address.dto.AddressListDto

interface AddressListener {

    fun selectItem(addressListDto: AddressListDto)


    fun edit(addressListDto: AddressListDto)


}