package com.example.kokako

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.model.WordDTO
import kotlinx.android.synthetic.main.activity_add_word.*
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.list_item.view.*

class AddWordActivity : AppCompatActivity() {
    lateinit var adapter: RecyclerAdapterWords
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)
        val list = ArrayList<WordDTO>()
        // Forcing the Soft Keyboard open
        var imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        var btnListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn_remove_add -> {
                    input_word.text.clear()
                    input_mean.text.clear()
                }

                R.id.btn_move_left -> {
                    input_word.requestFocus()
//  새로운 카드 없으면 만들고 커서이동
                }
                R.id.btn_move_right -> {
                    input_mean.requestFocus()

                    if (rv_word!=null) {
                        Toast.makeText(this,""+rv_list_item[0],Toast.LENGTH_LONG).show()
                        input_mean.setNextFocusRightId(0)
//                        rv_list_item[0].rv_word.requestFocus()
//                        rv_list_item[0].toString()
                    } else {
                        Toast.makeText(this,"또 있다.",Toast.LENGTH_LONG).show()
                    }
                    // 오른쪽 없으면 새로운 칸 추가 if문
                }
                // data없으면  무시하기
//                최신순서 맨위로
                R.id.btn_add_word -> {
                    input_word.text.toString().trim()
                    input_mean.text.toString().trim()
//                    if (input_word.text.equals("") || input_mean.text.equals("")) {
                    if(input_mean.text.toString().isEmpty() || input_word.text.toString().isEmpty()){
                        Toast.makeText(this,"데이터를 올바르게 입력",Toast.LENGTH_SHORT).show()
//                        버튼 활성화 안되게
//                        btn_add_word.isClickable = false
//                        btn_add_word.isEnabled = false
                    } else {
//                        btn_add_word.isClickable = true
//                        btn_add_word.isEnabled = true
                        list.add(WordDTO(input_word.text.toString(), input_mean.text.toString()))
                        adapter = RecyclerAdapterWords(list)
//                        var recyclerView = Recy.adapter = adapter
//                        val manager = LinearLayoutManager(this)
//                        manager.reverseLayout = true
//                        manager.stackFromEnd = true
//                        recy
                        rv_list_item.adapter = adapter
                        input_word.text.clear()
                        input_mean.text.clear()
                        input_word.requestFocus()
                    }
                }
            }
        }
        btn_remove_add.setOnClickListener(btnListener)
        btn_move_left.setOnClickListener(btnListener)
        btn_move_right.setOnClickListener(btnListener)
        btn_add_word.setOnClickListener(btnListener)


/*        list.add(WordDTO("치킨", "콜라"))
        list.add(WordDTO("치킨2", "콜라2"))
        list.add(WordDTO("치킨3", "콜라3"))
        list.add(WordDTO("치킨4", "콜라4"))
        list.add(WordDTO("치킨5", "콜라5"))
        list.add(WordDTO("치킨6", "콜라6"))
        list.add(WordDTO("치킨7", "콜라7"))
        list.add(WordDTO("치킨8", "콜라8"))
        list.add(WordDTO("치킨9", "콜라9"))
        list.add(WordDTO("치킨10", "콜라10"))
        list.add(WordDTO("치킨11", "콜라11"))
        list.add(WordDTO("치킨12", "콜라12"))
        list.add(WordDTO("치킨13", "콜라13"))
        list.add(WordDTO("치킨14", "콜라14"))*/
        Log.v("  ","ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ")


    }

    // 뒤로가기 다이얼로그
}