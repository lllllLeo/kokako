package com.yj.addwords

interface ItemTouchHelperListener {
    fun onItemMove(from_position : Int, to_position : Int): Boolean
}