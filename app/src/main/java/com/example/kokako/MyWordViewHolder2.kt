package com.example.kokako

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.ItemWordDTO

class MyWordViewHolder2 : RecyclerView.ViewHolder {
    var headerTitle : TextView? = null
    var refferalItem : ItemWordDTO? = null
    var btnExpandToggle : ImageView? = null

    constructor (itemView: View) : super(itemView) {
        headerTitle = itemView.findViewById(R.id.header_title)
        btnExpandToggle = itemView.findViewById(R.id.btn_expand_toggle)
    }
}
