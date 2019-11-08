package com.orange.tpms.mmySql

import java.io.Serializable

class Item: Serializable {
    var id: Long = 0
    var make: String
    var model: String? = null
    var year: String? = null
    var orangePart: String? = null
    var rfNum: String? = null
    var lfNum: String? = null
    var makeImg: String? = null

    constructor(){
        make = ""
    }

    constructor(id: Long, make: String, model: String, year: String, orangePart: String,
                rfNum: String, lfNum: String, img: String){
        this.id = id
        this.make = make
        this.model = model
        this.year = year
        this.orangePart = orangePart
        this.rfNum = rfNum
        this.lfNum = lfNum
        this.makeImg = img
    }

}