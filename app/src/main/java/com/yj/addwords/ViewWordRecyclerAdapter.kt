package com.yj.addwords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yj.addwords.model.Word

class ViewWordRecyclerAdapter(viewWordRecyclerViewInterface: ViewWordRecyclerViewInterface): RecyclerView.Adapter<ViewWordViewHolder>() {
    private var                     wordDatas = ArrayList<Word>()
    private var                     viewWordRecyclerViewInterface : ViewWordRecyclerViewInterface? = null
    private var                     check = 0
//    private var                     checkboxList = ArrayList<CheckBoxData>()
    private var                     visibilityOptions = 0
    private var                     currentLongClickPosition : Int? = null

    init {
        this.viewWordRecyclerViewInterface = viewWordRecyclerViewInterface
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewWordViewHolder {
        return ViewWordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_word_list, parent, false), this.viewWordRecyclerViewInterface!!)
    }

//    한 페이지에 보이는 12항목. 12번 bind됨 / 그리고 위아래 스크롤할 때 마다 bind
//    이전에 맨밑까지 스크롤하고 뒤로가기하면 맨마지막꺼까지 bind함
    override fun onBindViewHolder(holder: ViewWordViewHolder, position: Int) {
        holder.bind(this.wordDatas, position, check, visibilityOptions, currentLongClickPosition)
    }

    override fun getItemCount(): Int { return wordDatas.size }

    fun submitList(words: ArrayList<Word>) {
        this.wordDatas = words
        notifyDataSetChanged() // 이거 넣으니까 별표나 뭐 눌러도 스크롤 안내려감 밑으로
    }

    fun getItem() : ArrayList<Word> {
        return this.wordDatas
    }

    fun updateCheckbox(num: Int, adapterPosition: Int) {
        check = num
        currentLongClickPosition = adapterPosition
    }

    // TODO: 2021-02-24 여기랑  activity showandhide 모여잇는데서 비포 현재 해서 하면될듯
    fun showAndHide(i: Int) {
        this.visibilityOptions = i
        notifyDataSetChanged()
    }
    companion object {
        private const val TAG = "TAG ViewWordRecyclerAdapter"
    }

}
