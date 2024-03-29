package com.yj.addwords

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import com.yj.addwords.model.Word

class AddRecyclerAdapter(addRecyclerViewInterface: AddRecyclerViewInterface): ListAdapter<Word, AddViewHolder>(MyDiffCallback) {
//class AddRecyclerAdapter(addRecyclerViewInterface: AddRecyclerViewInterface): RecyclerView.Adapter<AddViewHolder>() {
    private var wordDatas = ArrayList<Word>()
    private var addRecyclerViewInterface : AddRecyclerViewInterface? = null
    // 생성자
    // 외부에서 들어온걸 여기에 장착
    init {
        this.addRecyclerViewInterface = addRecyclerViewInterface
    }

    // 뷰와 뷰홀더가 묶였을때
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        holder.bind(this.wordDatas,position)
        // 여기서 클릭리스너안함. MyViewHolder에 View.OnClickListener가 있으니까 발동된다
    }
    // 뷰 홀더가 생성 되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        // 연결할 레이아웃 설정
//        return AddViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_add_list_item, parent, false), this.addRecyclerViewInterface!!)

        val addViewHolder = AddViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_add_list_item, parent, false), this.addRecyclerViewInterface!!)
        addViewHolder.wordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                Log.d("     TAG","===== AddViewHolder afterTextChanged wordEditText called")

                if (addViewHolder.wordEditText.tag != null) {
                    val position: Int = addViewHolder.wordEditText.tag as Int
                    Log.d("     TAG",
                        "===== AddViewHolder wordEditText position : $position 값 : ${s.toString()}")
                    wordDatas[position].word = s.toString()
                     if (addViewHolder.wordEditText.text.toString().trim().isEmpty()) {
                            addViewHolder.wordEditText.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CD1818"))
                    } else {
                         addViewHolder.wordEditText.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
                    }
                }
            }
        })
        addViewHolder.meanEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                Log.d("     TAG","===== AddViewHolder afterTextChanged meanEditText called")
                if (addViewHolder.meanEditText.tag != null) {
                    val position: Int = addViewHolder.meanEditText.tag as Int
                    Log.d("     TAG",
                        "===== AddViewHolder meanEditText position : $position 값 : ${s.toString()}")
                    wordDatas[position].mean = s.toString()
                    if (addViewHolder.meanEditText.text.toString().trim().isEmpty()) {
                        addViewHolder.meanEditText.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CD1818"))
                    } else {
                        addViewHolder.meanEditText.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
                    }
                }
            }
        })
        return addViewHolder
    }
    fun getItem(): ArrayList<Word> = this.wordDatas

    override fun getItemCount(): Int = wordDatas.size

    fun submitDataList(word: ArrayList<Word>){
        this.wordDatas = word
        notifyDataSetChanged()
//        notifyItemInserted()
    }
    fun removeItem(position: Int) {
        wordDatas.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)

    }
    fun addItem(word: Word) {
        /*wordDatas.add(word)
        notifyDataSetChanged()*/
        /*wordDatas.add(0, word)
        notifyItemInserted(0)*/
        // 맨 위로 추가
        wordDatas.add(wordDatas.size, word)
        notifyItemInserted(wordDatas.size)
    }
}
