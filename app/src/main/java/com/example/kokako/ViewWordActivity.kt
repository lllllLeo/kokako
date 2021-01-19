package com.example.kokako

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kokako.databinding.ActivityToolbarBinding
import com.example.kokako.databinding.ActivityViewWordBinding
import com.example.kokako.model.Word
import com.example.kokako.viewModel.WordViewModel
import kotlinx.android.synthetic.main.activity_view_word.*
//  Log.d("     TAG", "===== AddWordActivity")
class ViewWordActivity : AppCompatActivity(), ViewWordRecyclerViewInterface {
    private lateinit var toolbarBinding: ActivityToolbarBinding
    private var _binding : ActivityViewWordBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewRecyclerAdapter : ViewWordRecyclerAdapter
    private var model : WordViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityViewWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val wordBookIdForView = intent.getLongExtra("wordBookIdForView",0)
        val sortItems = resources.getStringArray(R.array.sort_array)
        val hideItems = resources.getStringArray(R.array.hide_array) // 발음 가리기 (3 items)
        toolbarBinding = binding.includeToolbar
        /*---- Tool Bar ----*/
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(false)   //커스터마이징 하기 위해 필요
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시 유무
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        toolbarBinding.toolbarTitle.text = "단어장 제목" // TODO 단어장 제목 추가

        Log.d("     TAG", "===== ViewWordActivity getExtra wordBookIdForView 값은 : $wordBookIdForView")

        model = ViewModelProvider(this, object :ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WordViewModel(application,wordBookIdForView) as T
            }
        }).get(WordViewModel::class.java)
        model?.wordList?.observe(this, {
            Log.d("     TAG", "===== ViewWordActivity observe IN")
            updateWordList(it)
            Log.d("     TAG", "===== ViewWordActivity observe OUT")
        })




        val sortArrayAdapter = object : ArrayAdapter<String>(this, R.layout.sort_spinner) {
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    (v.findViewById<View>(R.id.sort_item_spinner) as TextView).text = getItem(count)
//                    (v.findViewById<View>(R.id.sort_item_spinner) as TextView).text = ""
//                    (v.findViewById<View>(R.id.sort_item_spinner) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int { return super.getCount() - 1 }
        }
        sortArrayAdapter.addAll(sortItems.toMutableList())
        sortArrayAdapter.add("정렬")
        sort_spinner.adapter = sortArrayAdapter
        sort_spinner.setSelection(sortArrayAdapter.count)
        sort_spinner.dropDownVerticalOffset = dipToPixels(45f).toInt()
        sort_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
            when(position) {
                0 -> { // 단어 오름차순
                }
                1 -> { // 단어 내림차순

                }
                2 -> { // 뜻 오름차순

                }
                3 -> { // 뜻 내림차순

                }
                4 -> { // 랜덤

                }
            }
        }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        val hideArrayAdapter = object : ArrayAdapter<String>(this, R.layout.hide_spinner) {
            @SuppressLint("CutPasteId")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    (v.findViewById<View>(R.id.hide_item_spinner) as TextView).text = ""
                    (v.findViewById<View>(R.id.hide_item_spinner) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int { return super.getCount() - 1 }
        }
        hideArrayAdapter.addAll(hideItems.toMutableList())
        hideArrayAdapter.add("가리기")
        hide_spinner.adapter = hideArrayAdapter
        hide_spinner.setSelection(hideArrayAdapter.count)
        hide_spinner.dropDownVerticalOffset = dipToPixels(45f).toInt()
        hide_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                when(position) {
                    0 -> { // 단어 가리기
                    }
                    1 -> { // 뜻 가리기

                    }
                    2 -> { // 랜덤

                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun updateWordList(word: List<Word>?) {
        Log.d("     TAG", "===== ViewWordActivity updateWordList IN")
        viewRecyclerAdapter = ViewWordRecyclerAdapter(this)
        viewRecyclerAdapter.submitList(word as ArrayList<Word>)
        rv_list_word_view.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            setHasFixedSize(true)
            adapter = viewRecyclerAdapter
        }
        Log.d("     TAG", "===== ViewWordActivity updateWordList OUT")
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_word_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mBuilder = AlertDialog.Builder(this)
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menu_edit -> {
//                누르면 바로 페이지 이동?
                mBuilder.setTitle("단어장 편집")
                    .setMessage("단어장을 편집하시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
//                        DB작업
                        dialog.dismiss()
                        finish()
                    })
                mBuilder.create().show()
            }
            R.id.menu_delete -> {
                mBuilder.setTitle("단어장 삭제")
                    .setMessage("단어장을 삭제하시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
//                        DB작업, MainActivity로
//                        val intent = Intent()
//                        setResult(Activity.RESULT_OK, intent)
                        dialog.dismiss()
                        finish()
                    })
                mBuilder.create().show()
            }
        }
        return true
    }
}