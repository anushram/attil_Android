package com.develop.sns.address.dto

import java.io.Serializable

class AddressListDto : Serializable {

    var lat: String = ""
    var lng: String = ""
    var createdAt: String = ""
    var createdAtTZ: String = ""
    var _id: String = ""
    var street: String = ""
    var phoneNumber: String = ""
    var doorNo: String = ""
    var landmark: String = ""
    var pinCode: String = ""
    var townORcity: String = ""


    override fun toString(): String {
        return _id
    }

    override fun equals(other: Any?): Boolean {
        if (other is AddressListDto) {
            return _id == other._id
        }
        return false
    }

    override fun hashCode(): Int {
        return _id.hashCode()
    }

}
