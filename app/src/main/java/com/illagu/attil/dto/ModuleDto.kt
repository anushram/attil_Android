package com.talentmicro.icanrefer.dto

import java.io.Serializable

/**
 * Created by Sujit on 31-08-2017.
 */
class ModuleDto : Serializable {
    var parentModuleId = 0
    var moduleId = 0
    var moduleTitle: String? = null
    var mobileNo: String? = null
    var userId: String? = null
    var isCompleted = 0

    override fun toString(): String {
        return moduleTitle!!
    }

    //For sublist validation
    override fun equals(o: Any?): Boolean {
        if (o is ModuleDto) {
            return moduleId == o.moduleId
        }
        return false
    }
}