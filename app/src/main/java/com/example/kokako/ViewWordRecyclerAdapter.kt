package com.example.kokako

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.Word

class ViewWordRecyclerAdapter(viewWordRecyclerViewInterface: ViewWordRecyclerViewInterface): RecyclerView.Adapter<ViewWordViewHolder>() {
    private var wordDatas = ArrayList<Word>()
    private var viewWordRecyclerViewInterface : ViewWordRecyclerViewInterface? = null

    init {
        this.viewWordRecyclerViewInterface = viewWordRecyclerViewInterface
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewWordViewHolder {
        return ViewWordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_view_list_item, parent, false), this.viewWordRecyclerViewInterface!!)
    }

    override fun onBindViewHolder(holder: ViewWordViewHolder, position: Int) {
        holder.bind(this.wordDatas, position)
    }

    override fun getItemCount(): Int { return wordDatas.size }
    fun submitList(words: ArrayList<Word>) {
        this.wordDatas = words
        notifyDataSetChanged() // 이거 넣으니까 별표나 뭐 눌러도 스크롤 안내려감 밑으로

    }

    fun getItem() : ArrayList<Word> {
        return this.wordDatas
    }

}
