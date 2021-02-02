package com.example.kokako

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.CheckBoxData
import com.example.kokako.model.Word
import kotlinx.android.synthetic.main.rv_view_list_item.view.*
import kotlin.collections.ArrayList

class ViewWordViewHolder(itemView: View, viewWordRecyclerViewInterface: ViewWordRecyclerViewInterface) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
    private var starButton = itemView.view_my_star
    private var wordTextView = itemView.view_my_word
    private var meanTextView = itemView.view_my_mean
    private var myWordBtnViewOption = itemView.my_word_btn_options
    private var wordCheckBox = itemView.view_my_check
    private var wordList = itemView.view_word_book_list
    private var viewWordRecyclerViewInterface : ViewWordRecyclerViewInterface? = null
//    private var                     checkboxList = ArrayList<CheckBoxData>()
    private var lilist = ArrayList<CheckBoxData>()
/*    companion object {
        var checkboxList = ArrayList<CheckBoxData>()
    }*/

    private var checkboxListToView = ArrayList<CheckBoxData>()

    init {
        starButton.setOnClickListener(this)
        myWordBtnViewOption.setOnClickListener(this)
        wordList.setOnLongClickListener(this)
        wordCheckBox.setOnClickListener(this)
        this.viewWordRecyclerViewInterface = viewWordRecyclerViewInterface
    }

    fun bind(
        wordDatas: ArrayList<Word>,
        position: Int,
        num: Int,
        checkboxList: ArrayList<CheckBoxData>
    ) {
        wordTextView.text = wordDatas[position].word
        meanTextView.text = wordDatas[position].mean


        if(num == 1) {
            wordCheckBox.visibility = View.VISIBLE
            starButton.visibility = View.GONE
            myWordBtnViewOption.visibility = View.INVISIBLE
        } else {
            wordCheckBox.visibility = View.GONE
            starButton.visibility = View.VISIBLE
            myWordBtnViewOption.visibility = View.VISIBLE

            if (wordDatas[position].bookMarkCheck == 1) {
                starButton.setBackgroundResource(R.drawable.star_visible)
            } else {
                starButton.setBackgroundResource(R.drawable.star_invisible)
            }
        }
        if (position >= checkboxList.size) {
            checkboxList.add(position, CheckBoxData(wordDatas[position].id, false))
        }
            checkboxListToView = checkboxList
        if (num == 0) {
            checkboxList[position].checked = false
        } else {
            wordCheckBox.isChecked = checkboxList[position].checked
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
            R.id.view_my_check -> {
                this.viewWordRecyclerViewInterface?.onCheckboxClicked(v, adapterPosition, checkboxListToView)
            }

        }
    }

    override fun onLongClick(v: View?): Boolean {
        this.viewWordRecyclerViewInterface?.onWordLongClicked(v!!, adapterPosition)
        return true
    }
}
