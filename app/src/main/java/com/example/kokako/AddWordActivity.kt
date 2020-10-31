package com.example.kokako

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_word.*

class AddWordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)

        // Forcing the Soft Keyboard open
        var imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        var btnListener = View.OnClickListener { view ->
            when(view.id){
                R.id.btn_move_left -> {
                    input_word.requestFocus()
                }
                R.id.btn_move_right -> {
                    input_mean.requestFocus()
                    // 오른쪽 없으면 새로운 칸 추가 if문
                }
                R.id.btn_add_word -> {
                    Toast.makeText(this,"왼쪾", Toast.LENGTH_LONG).show()
                }
            }
        }
        btn_move_left.setOnClickListener(btnListener)
        btn_move_right.setOnClickListener(btnListener)
        btn_add_word.setOnClickListener(btnListener)
    }

    // 뒤로가기 다이얼로그
}