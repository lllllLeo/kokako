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
    private var myWordBtnViewOption = itemView.my_word_btn_options
    private var viewWordRecyclerViewInterface : ViewWordRecyclerViewInterface? = null

    init {
        starButton.setOnClickListener(this)
        myWordBtnViewOption.setOnClickListener(this)
        this.viewWordRecyclerViewInterface = viewWordRecyclerViewInterface
    }

    fun bind(wordDatas: ArrayList<Word>, position: Int) {
        wordTextView.text = wordDatas[position].word
        meanTextView.text = wordDatas[position].mean
        if (wordDatas[position].bookMarkCheck == 1) {
            starButton.setBackgroundResource(R.drawable.star_visible)
        } else {
            starButton.setBackgroundResource(R.drawable.star_invisible)
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.my_word_btn_options -> {
                this.viewWordRecyclerViewInterface?.onPopupMenuWordClicked(v, myWordBtnViewOption, adapterPosition)
            }
            R.id.view_my_star -> {
                this.viewWordRecyclerViewInterface?.onStarClicked(v, adapterPosition)
            }
        }
    }

}
