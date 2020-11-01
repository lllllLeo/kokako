package com.example.kokako

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kokako.model.WordDTO
import kotlinx.android.synthetic.main.activity_add_word.*
import kotlinx.android.synthetic.main.list_item.*
import java.text.FieldPosition

class AddWordActivity : AppCompatActivity(), MyRecyclerViewInterface {
    lateinit var myRecyclerAdapter: MyRecyclerAdapter
    var wordDto = ArrayList<WordDTO>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)

        // Forcing the Soft Keyboard open
        var imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        var btnListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn_remove_text -> {
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

                        // adapter 인스턴스 생성
                        wordDto.add(WordDTO(input_word.text.toString(), input_mean.text.toString()))
                        myRecyclerAdapter = MyRecyclerAdapter(this)
                        myRecyclerAdapter.submitList(this.wordDto)

                        // 리사이클러뷰 설정
                        rv_list_item.apply {
                            //
                            layoutManager = LinearLayoutManager(this@AddWordActivity, LinearLayoutManager.VERTICAL, true)
                            (layoutManager as LinearLayoutManager).stackFromEnd = true

                            // 어답터 장착
                            adapter = myRecyclerAdapter
                        }

                        /*list.add(WordDTO(input_word.text.toString(), input_mean.text.toString()))
                        adapter = RecyclerAdapterWords(list)
                        rv_list_item.adapter = adapter
                        Log.v("","buttonbuttonbuttonbutton")
                        val llm = LinearLayoutManager(this)
                        llm.reverseLayout = true  //reverseLayout: 아이템이 보이는 방향. true 지정시 아래에서 위로 올라감
                        llm.stackFromEnd = true // 이게 false였을때 지우면 안올라갔는데 true하니까 삭제된 자리 빼고 위로 채워짐
                        rv_list_item.layoutManager = llm
//                      item이 추가되거나 삭제될 때 RecyclerView의 크기가 변경될 수도 있고, 그렇게 되면 계층 구조의 다른 View 크기가 변경될 가능성이 있기 때문이다. 특히 item이 자주 추가/삭제되면 오류가 날 수도 있기에 setHasFixedSize true를 설정
                        rv_list_item.setHasFixedSize(true)*/

                        input_word.text.clear()
                        input_mean.text.clear()
                        input_word.requestFocus()
                    }
                }
            }
        }
        btn_remove_text.setOnClickListener(btnListener)
        btn_move_left.setOnClickListener(btnListener)
        btn_move_right.setOnClickListener(btnListener)
        btn_add_word.setOnClickListener(btnListener)

    }

    override fun onItemClicked(position: Int) {
        TODO("Not yet implemented")
    }

    // 뒤로가기 다이얼로그
}