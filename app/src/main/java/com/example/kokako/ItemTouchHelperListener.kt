package com.example.kokako

interface ItemTouchHelperListener {
    fun onItemMove(from_position : Int, to_position : Int): Boolean
}