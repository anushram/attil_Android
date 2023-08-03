package com.illagu.attil.signup.dto

import java.io.Serializable

class SignUpDto : Serializable {

    var id: String? = null
    var mobileNo: String? = null
    var userId: String? = null
    var gender: String? = null
    var password: String? = null
    var isPassword: Boolean? = false

    override fun equals(o: Any?): Boolean {
        if (o is SignUpDto) {
            return id.equals(o.id)
        }
        return false
    }

    override fun toString(): String {
        return userId!!
    }
}