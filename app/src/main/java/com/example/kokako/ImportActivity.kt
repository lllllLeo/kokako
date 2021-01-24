package com.example.kokako

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.kokako.databinding.ActivityImportBinding
import com.example.kokako.databinding.ActivityToolbarBinding

class ImportActivity : AppCompatActivity() {
    private lateinit var toolbarBinding: ActivityToolbarBinding
    private var _binding : ActivityImportBinding? = null
    private val binding get() = _binding!!
    private var imm : InputMethodManager? = null   // 여기 선언한다고 lateinit 함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityImportBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        toolbarBinding = binding.includeToolbar

        /*---- Tool Bar ----*/
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        toolbarBinding.toolbarTitle.setText("단어장 가져오기")

        var imText : String = "hi,안녕\nbye,잘가\nbeer,맥주"
        var importText = binding.importText.toString()

        binding.importPreview.setOnClickListener{
            var textSeparator : String = binding.textSeparator.text.toString()
            var previewText = imText.split(textSeparator,"\n")
//            Toast.makeText(context,""+previewText[0]+"|"+previewText[1], Toast.LENGTH_SHORT).show()

//       미리보기를 좀 다르게 어떻게 할까나 / 실시간, 똑같은 뷰,
/*            val mBuilder = AlertDialog.Builder(it.context)
            mBuilder.setMessage(previewText[0])*/
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                finish()
            }
            R.id.menu_check -> {
                val mBuilder = AlertDialog.Builder(this)
                mBuilder.setTitle("단어장으로 추가")
                    .setMessage("가져온 텍스트를 단어장으로 만드시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
//                        DB작업
                        // FIXME: 2021-01-23 Toast?  snackBar?
                        Toast.makeText(this, "추가 완료", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        finish()
                    })
                val mDialog = mBuilder.create()
                mDialog.show()
            }
        }
        return true
    }
}