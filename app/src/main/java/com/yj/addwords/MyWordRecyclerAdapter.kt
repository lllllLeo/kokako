package com.yj.addwords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yj.addwords.model.WordBook
import com.yj.addwords.viewModel.WordViewModel

// Adapter 캐리어 ViewHolder는 인터셉터
class MyWordRecyclerAdapter(myWordListRecyclerViewInterface: MyWordListRecyclerViewInterface) : RecyclerView.Adapter<MyWordViewHolder>(), ItemTouchHelperListener{
    private var wordBookDatas = ArrayList<WordBook>()
    private var wordModel : WordViewModel? = null
    private var myWordListRecyclerViewInterface : MyWordListRecyclerViewInterface? = null
    init {
        this.myWordListRecyclerViewInterface = myWordListRecyclerViewInterface
    }

    override fun getItemCount(): Int = wordBookDatas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWordViewHolder {
        return MyWordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_wordbook_list, parent, false), this.myWordListRecyclerViewInterface!!)
    }

//    하나하나 생성될 떄 마다 호출이되니까 포지션을 안다
    override fun onBindViewHolder(holder: MyWordViewHolder, position: Int) {
        holder.bind(this.wordBookDatas[position])
    }

//    내 단어장들 불러오는 메소드
    fun getItem():ArrayList<WordBook> = this.wordBookDatas

    fun submitList(wordBook: ArrayList<WordBook>) {
        this.wordBookDatas = wordBook
        notifyDataSetChanged()
    }

    override fun onItemMove(from_position: Int, to_position: Int): Boolean {
//        val wordBook: WordBook = wordBookDatas[from_position]
//        wordBookDatas.removeAt(from_position)
//        wordBookDatas.add(to_position, wordBook)
        val temp = wordBookDatas[from_position].itemOrder
        wordBookDatas[from_position].itemOrder = wordBookDatas[to_position].itemOrder
        wordBookDatas[to_position].itemOrder = temp
        wordBookDatas.sort()
        notifyItemMoved(from_position, to_position)
        return true
    }
}