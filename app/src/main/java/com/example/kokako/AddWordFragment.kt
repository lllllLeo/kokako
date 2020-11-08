package com.example.kokako

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kokako.model.WordDTO
import kotlinx.android.synthetic.main.fragment_add_word.*

class AddWordFragment : Fragment(), MyRecyclerViewInterface {
    private lateinit var mainViewModel : MainViewModel
    lateinit var myRecyclerAdapter: MyRecyclerAdapter
    var wordDto = ArrayList<WordDTO>()
    var wordCount: Int = 0
    var currentCount: Int = 0
    var countString: String? = null
    var tv_wordCount: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_word, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.run {
            mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(MainViewModel::class.java)
        }










        tv_wordCount = view.findViewById<TextView>(R.id.word_count)

        //        add Divider in RecyclerView
        rv_list_item.addItemDecoration(DividerItemDecoration(view.context,LinearLayoutManager.VERTICAL))



        // Forcing the Soft Keyboard open
        var imm: InputMethodManager =
            activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        countString = "$currentCount/$wordCount"
        Toast.makeText(this.context, countString, Toast.LENGTH_SHORT).show()
        tv_wordCount?.text = countString

        var btnListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.delete_all -> {
                    input_word.text.clear()
                    input_mean.text.clear()
                    input_word.requestFocus()
                }
                R.id.btn_remove_text -> {
                    if (input_word.isFocused) {
                        input_word.text.clear()
                    } else if (input_mean.isFocused)
                        input_mean.text.clear()
                }
                R.id.btn_move_left -> {
                    input_word.requestFocus()
//  새로운 카드 없으면 만들고 커서이동
                }
                R.id.btn_move_right -> {
                    input_mean.requestFocus()
                }
// data없으면  무시하기
                R.id.btn_add_word -> {
                    input_word.text.toString().trim()
                    input_mean.text.toString().trim()
//                    if (input_word.text.equals("") || input_mean.text.equals("")) {
                    if (input_mean.text.toString().isEmpty() || input_word.text.toString()
                            .isEmpty()
                    ) {
                        Toast.makeText(this.context, "데이터를 올바르게 입력", Toast.LENGTH_SHORT).show()
//                        버튼 활성화 안되게
//                        btn_add_word.isClickable = false
//                        btn_add_word.isEnabled = false
                    } else {
//                        btn_add_word.isClickable = true
//                        btn_add_word.isEnabled = true
                        wordDto.add(WordDTO(input_word.text.toString(), input_mean.text.toString()))
                        myRecyclerAdapter = MyRecyclerAdapter(this)
                        myRecyclerAdapter.submitList(this.wordDto)

                        // 리사이클러뷰 설정
                        rv_list_item.apply {
                            layoutManager = LinearLayoutManager(context,
                                LinearLayoutManager.VERTICAL,
                                true)
//                            (layoutManager as LinearLayoutManager).stackFromEnd = true
                            // 어답터 장착
                            adapter = myRecyclerAdapter
                        }
                        input_word.text.clear()
                        input_mean.text.clear()
                        input_word.requestFocus()
//                        Toast.makeText(this,"현재 단어 갯수"+wordDto.size,Toast.LENGTH_SHORT).show()
                        wordCount++
                        countString = "$currentCount/$wordCount"
                        tv_wordCount?.text = countString
                        Toast.makeText(this.context, "wordCount는 " + wordCount, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        delete_all.setOnClickListener(btnListener)
        btn_remove_text.setOnClickListener(btnListener)
        btn_move_left.setOnClickListener(btnListener)
        btn_move_right.setOnClickListener(btnListener)
        btn_add_word.setOnClickListener(btnListener)
    }

    /*
    * 이 액티비티에서 뷰홀더에서 onClick()된걸 아니까 이 액티비티에서 클릭처리를 할 수 있다
    * */
    override fun onRemoveClicked(it: View, position: Int) {
        Log.d(" TAG ", "AddWordActivity -- onItemClicked() called")
        val mBuilder = AlertDialog.Builder(it.context)
        mBuilder.setTitle("삭제")
            .setMessage("단어 : " + wordDto[position].word.toString() + "\n뜻 : " + wordDto[position].mean.toString() + "\n이 항목을 삭제하시겠습니까?")
            .setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, which ->
                    myRecyclerAdapter?.removeWord(wordDto, position)
//                    Toast.makeText(this,"삭제된 후 단어 개수"+wordDto.size,Toast.LENGTH_SHORT).show()
                    wordCount--
                    countString = "$currentCount/$wordCount"
                    tv_wordCount?.text = countString
//                    Toast.makeText(this,"wordCount는 "+wordCount,Toast.LENGTH_SHORT).show()
                })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
        mBuilder.show()
    }
// 뒤로가기 다이얼로그
}


