package com.example.kokako

import android.view.View
import java.text.FieldPosition

interface MyWordListRecyclerViewInterface {
    fun onRemoveClicked(v : View, position: Int)
    fun onViewClicked(v: View, adapterPosition: Int)
}