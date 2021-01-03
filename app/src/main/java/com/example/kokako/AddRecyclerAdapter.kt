package com.example.kokako

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.Word

class AddRecyclerAdapter(addRecyclerViewInterface: AddRecyclerViewInterface): RecyclerView.Adapter<AddViewHolder>() {

    private var wordDtoData = ArrayList<Word>()

    private var addRecyclerViewInterface : AddRecyclerViewInterface? = null

    // 생성자
    // 외부에서 들어온걸 여기에 장착
    init {
        this.addRecyclerViewInterface = addRecyclerViewInterface
    }

    // 뷰 홀더가 생성 되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        // 연결할 레이아웃 설정
        return AddViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_add_list_item, parent, false), this.addRecyclerViewInterface!!)
    }
    // 뷰와 뷰홀더가 묶였을때
    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        holder.bind(this.wordDtoData,position)
        // 여기서 클릭리스너안함. MyViewHolder에 View.OnClickListener가 있으니까 발동된다
    }
    // 목록의 수
    override fun getItemCount(): Int { return wordDtoData.size }

    // 외부에서 데이터 넘기기, 단어, 뜻 추가시킴
    fun submitList(word: ArrayList<Word>){
        this.wordDtoData = word
    }

    fun removeWord(word: ArrayList<Word>, position: Int){
        word.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, word.size)
//        DefaultItemAnimator()
    }


}