package com.example.kokako

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.ItemWordDTO

// Adapter 캐리어 ViewHolder는 인터셉터
class MyWordRecyclerAdapter2: RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //    public static final
    companion object {
        const val HEADER: Int = 0
        const val CHILD: Int = 1
    }

    private var data : ArrayList<ItemWordDTO>

    constructor (data : ArrayList<ItemWordDTO>) {
        this.data = data
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWordViewHolder2 {
        var view : View? = null
        var context : Context = parent.context
        var dp  = context.resources.displayMetrics.density
        var subItemPaddingLeft = (24 * dp).toInt()
        var subItemPaddingTopAndBottom = (6 * dp).toInt()

        when (viewType){
            HEADER -> {
                var inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.list_header,parent,false)
                var header = MyWordViewHolder2(view)
                return header
            }
//            단어 개수, 퍼센트 추가해야함
            CHILD -> {
                var itemTextView = TextView(context)
                itemTextView.setPadding(subItemPaddingLeft, subItemPaddingTopAndBottom,0,subItemPaddingTopAndBottom)
                itemTextView.setTextColor(Color.parseColor("#000000"))
                itemTextView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                return MyWordViewHolder2(itemTextView)
            }
        }
//        예제에선 null
        return MyWordViewHolder2(LayoutInflater.from(parent.context).inflate(R.layout.rv_wordbook_list, parent, false))
    }

//    하나하나 생성될 떄 마다 호출이되니까 포지션을 안다
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.bind(this.myWordListDTO[position])
        val itemWordDTO = data[position]
        when (itemWordDTO.type) {
            HEADER -> {
                val itemController : MyWordViewHolder2 = holder as MyWordViewHolder2
                itemController.refferalItem = itemWordDTO
                itemController.headerTitle?.text = itemWordDTO.text
                if (itemWordDTO.invisibleChildren == null) {
                    itemController.btnExpandToggle?.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    itemController.btnExpandToggle?.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
                itemController.btnExpandToggle?.setOnClickListener(View.OnClickListener {
                    if(itemWordDTO.invisibleChildren == null) {
                        itemWordDTO.invisibleChildren = ArrayList<ItemWordDTO>()
                        var count = 0
                        val pos = data.indexOf(itemController.refferalItem)
                        while (data.size > pos + 1 && data[pos + 1].type == CHILD) {
                            itemWordDTO.invisibleChildren?.add(data.removeAt(pos + 1))
                            count++
                        }
                        notifyItemRangeRemoved(pos + 1, count)
                        itemController.btnExpandToggle?.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    } else {
                        val pos = data.indexOf(itemController.refferalItem)
                        var index = pos + 1
                        for (i in itemWordDTO.invisibleChildren!!) {
                            data.add(index, i)
                            index++
                        }
                        notifyItemRangeInserted(pos + 1, index - pos - 1)
                        itemController.btnExpandToggle?.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                        itemWordDTO.invisibleChildren = null
                    }
                })
            } // break
            CHILD -> {
                var itemTextView = holder.itemView as TextView
                itemTextView.text = data[position].text
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun getItemCount(): Int {
        return this.data.size
    }


}

