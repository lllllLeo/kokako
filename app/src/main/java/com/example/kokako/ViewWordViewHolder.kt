package com.example.kokako

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.Word
import kotlinx.android.synthetic.main.rv_view_list_item.view.*
import java.util.ArrayList

class ViewWordViewHolder(itemView: View, viewWordRecyclerViewInterface: ViewWordRecyclerViewInterface) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private var starButton = itemView.view_my_star
    private var wordTextView = itemView.view_my_word
    private var meanTextView = itemView.view_my_mean
    private var viewWordRecyclerViewInterface : ViewWordRecyclerViewInterface? = null

    init {
        this.viewWordRecyclerViewInterface = viewWordRecyclerViewInterface
    }

    fun bind(wordDatas: ArrayList<Word>, position: Int) {
        wordTextView.text = wordDatas[position].word
        meanTextView.text = wordDatas[position].mean
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}
