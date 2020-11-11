package com.example.kokako.model

class ItemWordDTO{
    var type : Int = 0
    var text : String? = null
    var invisibleChildren : ArrayList<ItemWordDTO>? = null


//    fun ItemWordDTO(){}
    init {

    }

    constructor (type : Int, text : String) {
        this.type = type
        this.text = text
    }

}