package com.develop.sns.address.dto

import java.io.Serializable

class AddressDto : Serializable {

    var addressId: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var addressLine1: String = ""
    var addressLine2: String = ""
    var city: String = ""
    var state: String = ""
    var country: String = ""
    var pinCode: String = ""
    var landMark: String = ""
    var type = 0

    var name: String = ""
    var phoneIsd: String = ""
    var phoneNo: String = ""

    var countryCode: String = ""
    var houseNo: String = ""
    var subHouseNo: String = ""
    var area: String = ""

    var halfAddress: String = ""

    override fun toString(): String {
        return addressId
    }

    override fun equals(other: Any?): Boolean {
        if (other is AddressDto) {
            return addressId == other.addressId
        }
        return false
    }

    override fun hashCode(): Int {
        return addressId.hashCode()
    }

}
